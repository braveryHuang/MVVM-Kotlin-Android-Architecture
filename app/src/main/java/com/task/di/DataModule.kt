
package com.task.di

import com.task.data.DataRepository
import com.task.data.DataRepositorySource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Tells Dagger this is a Dagger module
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    /**
     * 将 DataRepository 类的实例绑定为 DataRepositorySource 接口的实现。
     *
     * 1. 抽象方法的参数：
     * dataRepository: DataRepository 是方法的参数，表示 Dagger 需要提供一个 DataRepository 的实例
     * Dagger 会自动寻找如何创建 DataRepository 实例（通常是通过构造函数注入）
     *
     * 2. 返回类型：
     * DataRepositorySource 是一个接口，是方法的返回类型
     * 这告诉 Dagger，当有地方需要 DataRepositorySource 类型的依赖时，应该提供一个 DataRepository 的实例
     *
     * 3. 抽象类和抽象方法：
     * 模块是抽象的，方法也是抽象的，因为 @Binds 只需要声明绑定关系，不需要方法体
     * Dagger 在编译时会生成实际的实现代码
     */
    @Binds
    @Singleton
    abstract fun provideDataRepository(dataRepository: DataRepository): DataRepositorySource
}
