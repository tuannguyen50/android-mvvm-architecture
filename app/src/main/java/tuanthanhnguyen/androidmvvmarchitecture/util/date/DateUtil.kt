/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Tuan Thanh Nguyen created this file.

package tuanthanhnguyen.androidmvvmarchitecture.util.date

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtil {

    fun convertDateTimeTimezoneOffsetFormatStringToLong(
        dateTimeTimezoneOffsetString: String
    ): Long {
        var localDateTimeTimezoneOffsetString: String = dateTimeTimezoneOffsetString

        // Normalize ISO 8601 UTC designator "Z" to "+0000" for SimpleDateFormat "Z" compatibility
        if (localDateTimeTimezoneOffsetString.endsWith("Z")) {
            localDateTimeTimezoneOffsetString =
                localDateTimeTimezoneOffsetString.dropLast(1) + "+0000"
        }
        // Strip the colon from timezone offsets (e.g., "-01:00" to "-0100") to match RFC 822
        // format
        else if (
            localDateTimeTimezoneOffsetString.matches(".*[+-]\\d{2}:\\d{2}$".toRegex())
        ) {
            val lastColon = localDateTimeTimezoneOffsetString.lastIndexOf(":")
            localDateTimeTimezoneOffsetString =
                localDateTimeTimezoneOffsetString.substring(0, lastColon) +
                        localDateTimeTimezoneOffsetString.substring(lastColon + 1)
        }
        // Parse using uppercase "Z" pattern, which offers full backward compatibility down to
        // API 1
        val simpleDateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ",
            Locale.getDefault()
        )
        try {
            val date: Date? = simpleDateFormat.parse(localDateTimeTimezoneOffsetString)
            return date?.getTime() ?: throw NullPointerException("Parsed date object is null")
        } catch (e: ParseException) {
            throw e
        }
    }

    fun convertTimeLongToDateTime(timeLong: Long): String {
        val date = Date(timeLong)

        val formatter = DateFormat.getDateTimeInstance(
            DateFormat.LONG,
            DateFormat.LONG,
            Locale.getDefault()
        )

        formatter.timeZone = TimeZone.getDefault()

        return formatter.format(date)
    }
}