# 1. PR hygiene checks
# - Check for Work In Progress (WIP) status
# If the contributor tagged it with [WIP], I will post a gentle warning so reviewers know it's not
# ready yet.
if github.pr_title.include?("[WIP]") || github.pr_title.downcase.start_with?("wip:")
  warn("This pull request is marked as Work in Progress (WIP) and is not ready for a final review.")
end

# - Check for a pull request description
# If someone leaves the description empty or writes less than 10 characters, I will block the
# merge until they fill it out.
if github.pr_body.nil? || github.pr_body.strip.length < 10
  fail("Please provide a detailed description of your changes in the PR body. A description is required.")
end

# - Check pull request size (Lines of code changed)
# If the PR touches more than 500 lines of code, I will flag it so the developer can consider
# breaking it into smaller chunks.
MAX_PR_SIZE = 500
if git.lines_of_code > MAX_PR_SIZE
  warn("This PR contains #{git.lines_of_code} lines of code. Please consider breaking it down into smaller, bite-sized pull requests (Ideal size is < #{MAX_PR_SIZE} lines).")
end

# - Check for target branch safeguard
# It's a quick heads-up if a PR is aiming straight for production branches like "main" or "master".
if github.branch_for_base == "main" || github.branch_for_base == "master"
  message("This Pull Request is targeting the primary production branch (`#{github.branch_for_base}`). Please ensure it has been thoroughly tested.")
end

# 2. Code analysis reports
# - Android lint report
# I'm using Dir.glob here to scan for all XML reports across the project, which works perfectly
# for multi-module setups.
lint_reports = Dir.glob("**/build/reports/lint-results-debug.xml")

if lint_reports.any?
  # General plugin configurations
  android_lint.filtering = true
  # Handled by your CI workflow instead
  android_lint.skip_gradle_task = true

  # Going through each found report file to post inline comments directly onto the PR lines.
  lint_reports.each do |report|
    android_lint.report_file = report
    android_lint.lint(inline_mode: true)
  end

  # This loop is crucial: I'm checking the severity of every found lint issue.
  # Since we set "abortOnError false" in Gradle, I need to manually trigger fail() to block the
  # merge if a "fatal" or "error" pops up.
  android_lint.issues.each do |issue|
    severity = issue.severity.to_s.downcase
    if severity == "error" || severity == "fatal"
      fail("Severe Android Lint issue found [#{issue.id}] in #{issue.file} at line #{issue.line}: #{issue.message}")
    end
  end

else
  warn("Android Lint report not found. Make sure \"./gradlew lintDebug\" ran successfully before the Danger step.")
end

# - Detekt (Kotlin static code analysis)
# Automatically scan for all detekt XML reports across the project
detekt_reports = Dir.glob("**/build/reports/detekt/*.xml")

if detekt_reports.any?
  detekt.filtering = true

  # Process each found detekt report file
  detekt_reports.each do |report|
    detekt.report_file = report
    detekt.lint
  end

  # Scanning through Detekt issues. If it finds critical errors or warnings, I'm throwing a fail
  # to keep the codebase clean.
  detekt.issues.each do |issue|
    severity = issue.severity.to_s.downcase
    if severity == "error" || severity == "warning"
      fail("Detekt code smell found [#{issue.id}] in #{issue.file}:#{issue.line} - #{issue.message}")
    end
  end
else
  warn("Detekt report not found. Make sure \"./gradlew detekt\" ran successfully before Danger.")
end

# 3. Testing report
# - JUnit test report (Unit tests)
# This default wildcard path safely grabs test XML results from any module in the workspace.
junit_reports = Dir.glob("**/build/test-results/**/*.xml")

if junit_reports.any?
  # Ensuring skipped test cases are still visible in the final table summary.
  junit.show_skipped_tests = true

  # Looping through all XML files to parse and aggregate the testing data properly.
  junit_reports.each do |report|
    junit.parse report
  end

  # This method call is required! It commands Danger to print a clean, readable Markdown summary
  # table right in the PR comment thread.
  junit.report

  # Passing unit tests is critical. If there are any failures, I will block the PR from being
  # merged.
  if junit.failures.count > 0
    fail("#{junit.failures.count} Unit test(s) failed! Please fix your tests before merging.")
  end
else
  warn("JUnit test reports not found. Make sure your unit test command (e.g., \"./gradlew test\") ran successfully before Danger.")
end

# 4. Compiler Warnings Section (Log Parsing)
log_file_path = "build_output.log"

if File.exist?(log_file_path)
  # Get the current working directory dynamically (works on both local machine and CI)
  current_dir = Dir.pwd

  File.foreach(log_file_path) do |line|
    clean_line = line.strip

    # 1. Regex to capture Kotlin Compiler Warnings (e.g., w: /path/to/File.kt: (10, 5): message)
    if line =~ /^w:\s+(.+?):\s*\((\d+),\s*\d+\):\s*(.+)$/
      file_path = $1
      line_number = $2.to_i
      message = $3

      # Sanitize path: Remove absolute local directory path, leaving only the relative path (e.g., app/src/...)
      relative_file = file_path.sub("#{current_dir}/", "")

      # Comment directly onto the specific line of code on the Pull Request
      warn("**Kotlin Compiler Warning:** #{message}", file: relative_file, line: line_number)

    # 2. Regex to capture Java Compiler Warnings (e.g., /path/to/File.java:15: warning: message)
    elsif line =~ /^(.+?):(\d+):\s*warning:\s*(.+)$/
      file_path = $1
      line_number = $2.to_i
      message = $3

      relative_file = file_path.sub("#{current_dir}/", "")
      warn("**Java Compiler Warning:** #{message}", file: relative_file, line: line_number)

    # 3. Capture general system build warnings without specific line numbers (e.g., Room exportSchema warning)
    elsif line.downcase.include?("warning:") || line.start_with?("w:")
      # Mask any local machine file paths inside the text for privacy and security
      clean_message = clean_line.gsub("#{current_dir}/", "")

      # Remove redundant 'w:' or 'warning:' prefixes from the beginning of the string for better formatting
      clean_message = clean_message.sub(/^w:\s*/i, "").sub(/^warning:\s*/i, "")

      warn("**Build Warning:** #{clean_message}")
    end
  end
end