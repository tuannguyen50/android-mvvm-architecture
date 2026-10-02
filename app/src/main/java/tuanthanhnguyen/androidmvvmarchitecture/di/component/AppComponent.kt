package tuanthanhnguyen.androidmvvmarchitecture.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import tuanthanhnguyen.androidmvvmarchitecture.AndroidMVVMArchitectureApp
import tuanthanhnguyen.androidmvvmarchitecture.di.module.main.MainActivityModule
import tuanthanhnguyen.androidmvvmarchitecture.di.module.AppModule
import tuanthanhnguyen.androidmvvmarchitecture.di.module.DatabaseModule
import tuanthanhnguyen.androidmvvmarchitecture.di.module.NetworkModule
import tuanthanhnguyen.androidmvvmarchitecture.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        ViewModelModule::class,
        MainActivityModule::class]
)
interface AppComponent : AndroidInjector<AndroidMVVMArchitectureApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(androidMVVMArchitectureApp: AndroidMVVMArchitectureApp)
}