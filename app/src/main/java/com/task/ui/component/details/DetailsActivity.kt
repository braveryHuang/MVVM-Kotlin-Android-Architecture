package com.task.ui.component.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.task.R
import com.task.RECIPE_ITEM_KEY
import com.task.data.Resource
import com.task.data.dto.recipes.RecipesItem
import com.task.databinding.DetailsLayoutBinding
import com.task.ui.base.BaseActivity
import com.task.utils.observe
import com.task.utils.toGone
import com.task.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AhmedEltaher
 */

@AndroidEntryPoint
class DetailsActivity : BaseActivity() {

    /**
     * Kotlin 的属性委托功能，结合 Android Jetpack 提供的 viewModels() 扩展函数，
     * 用于在 Activity 或 Fragment 中获取 ViewModel 实例。这是现代 Android 开发中获取 ViewModel 的标准方式。
     *
     * viewModels() 是 Android Jetpack 中 androidx.fragment:fragment-ktx 或
     * androidx.activity:activity-ktx 库提供的扩展函数，它返回一个委托对象，负责创建和管理 ViewModel 实例。
     *
     * 当您使用 by viewModels() 时，以下是背后发生的事情：
     * 1. 延迟初始化：ViewModel 不会立即创建，而是在第一次访问属性时才初始化
     * 2. 生命周期感知：委托会自动将 ViewModel 与 Fragment 或 Activity 的生命周期关联
     * 3. 实例管理：委托负责在适当的 ViewModelStore 中存储和检索 ViewModel 实例
     * 4. 配置变更处理：当配置变更（如屏幕旋转）发生时，委托确保返回现有的 ViewModel 实例，而不是创建新的
     */
    private val viewModel: DetailsViewModel by viewModels()

    /**
     * View Binding 的工作原理
     *
     * 1. 自动生成：
     * 当启用 View Binding 功能时，Android Gradle 插件会为每个 XML 布局文件自动生成一个绑定类
     * 例如，details_layout.xml 会生成 DetailsLayoutBinding 类
     *
     * 2. 命名转换：
     *  XML 文件名转换为绑定类名遵循以下规则：
     *  1). 下划线转为驼峰命名法 (例如: details_layout.xml → DetailsLayout)
     *  2). 添加 "Binding" 后缀 (例如: DetailsLayout → DetailsLayoutBinding)
     *
     * 3. 视图引用：
     *  1). 绑定类包含对布局中所有具有 ID 的视图的直接引用
     *  2). ID 名称也会从下划线命名转换为驼峰命名
     * 例如: text_title ID 在绑定类中变为 textTitle 属性
     */
    private lateinit var binding: DetailsLayoutBinding
    private var menu: Menu? = null


    override fun initViewBinding() {
        // 第1步：通过布局膨胀器创建绑定对象
        binding = DetailsLayoutBinding.inflate(layoutInflater)

        // 第2步：获取根视图
        val view = binding.root

        // 第3步：将根视图设置为内容视图
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * 1. 从 Intent 中尝试获取键为 RECIPE_ITEM_KEY 的 Parcelable 数据
         * 2. 如果数据存在，则将其转换为 RecipesItem 类型
         * 3. 如果数据不存在（返回 null），则创建一个新的空 RecipesItem 实例
         * 4. 将获取到的 RecipesItem（或新创建的）传递给 ViewModel 的 initIntentData 方法进行处理
         *
         * 这行代码体现了 MVVM 架构模式，其中:
         *      1. Activity/Fragment 负责 UI 和用户交互
         *      2. ViewModel 负责处理和准备数据
         *      3. 数据从 Intent 传递到 ViewModel，而不是直接在 Activity 中处理
         */
        viewModel.initIntentData(intent.getParcelableExtra(RECIPE_ITEM_KEY) ?: RecipesItem())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        this.menu = menu
        viewModel.isFavourites()
        return true
    }

    fun onClickFavorite(mi: MenuItem) {
        mi.isCheckable = false
        if (viewModel.isFavourite.value?.data == true) {
            viewModel.removeFromFavourites()
        } else {
            viewModel.addToFavourites()
        }
    }

    override fun observeViewModel() {
        observe(viewModel.recipeData, ::initializeView)
        observe(viewModel.isFavourite, ::handleIsFavourite)
    }

    private fun handleIsFavourite(isFavourite: Resource<Boolean>) {
        when (isFavourite) {
            is Resource.Loading -> {
                binding.pbLoading.toVisible()
            }

            is Resource.Success -> {
                isFavourite.data?.let {
                    handleIsFavouriteUI(it)
                    menu?.findItem(R.id.add_to_favorite)?.isCheckable = true
                    binding.pbLoading.toGone()
                }
            }

            is Resource.DataError -> {
                menu?.findItem(R.id.add_to_favorite)?.isCheckable = true
                binding.pbLoading.toGone()
            }
        }
    }

    private fun handleIsFavouriteUI(isFavourite: Boolean) {
        menu?.let {
            it.findItem(R.id.add_to_favorite)?.icon =
                if (isFavourite) {
                    ContextCompat.getDrawable(this, R.drawable.ic_star_24)
                } else {
                    ContextCompat.getDrawable(this, R.drawable.ic_outline_star_border_24)
                }
        }
    }

    private fun initializeView(recipesItem: RecipesItem) {
        binding.tvName.text = recipesItem.name
        binding.tvHeadline.text = recipesItem.headline
        binding.tvDescription.text = recipesItem.description
        Picasso.get().load(recipesItem.image).placeholder(R.drawable.ic_healthy_food_small)
            .into(binding.ivRecipeImage)

    }
}
