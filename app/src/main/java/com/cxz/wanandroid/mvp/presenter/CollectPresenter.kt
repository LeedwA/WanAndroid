package com.cxz.wanandroid.mvp.presenter

import com.cxz.wanandroid.base.BasePresenter
import com.cxz.wanandroid.http.exception.ExceptionHandle
import com.cxz.wanandroid.http.function.RetryWithDelay
import com.cxz.wanandroid.mvp.contract.CollectContract
import com.cxz.wanandroid.mvp.model.CollectModel

/**
 * Created by chenxz on 2018/6/9.
 */
class CollectPresenter : BasePresenter<CollectContract.View>(), CollectContract.Presenter {

    private val collectModel by lazy {
        CollectModel()
    }

    override fun getCollectList(page: Int) {
        mView?.showLoading()
        val disposable = collectModel.getCollectList(page)
                .retryWhen(RetryWithDelay())
                .subscribe({ results ->
                    mView?.run {
                        if (results.errorCode != 0) {
                            showError(results.errorMsg)
                        } else {
                            setCollectList(results.data)
                        }
                        hideLoading()
                    }
                }, { t ->
                    mView?.apply {
                        hideLoading()
                        showError(ExceptionHandle.handleException(t))
                    }
                })
        addSubscription(disposable)
    }

    override fun removeCollectArticle(id: Int, originId: Int) {
        val disposable = collectModel.removeCollectArticle(id, originId)
                .retryWhen(RetryWithDelay())
                .subscribe({ results ->
                    mView?.run {
                        if (results.errorCode != 0) {
                            showError(results.errorMsg)
                        } else {
                            showRemoveCollectSuccess(true)
                        }
                        hideLoading()
                    }
                }, { t ->
                    mView?.apply {
                        hideLoading()
                        showError(ExceptionHandle.handleException(t))
                    }
                })
        addSubscription(disposable)
    }

}