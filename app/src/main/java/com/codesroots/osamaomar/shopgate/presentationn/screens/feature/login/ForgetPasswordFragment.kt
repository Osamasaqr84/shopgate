package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.codesroots.osamaomar.shopgate.R
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.register.RegisterViewModel
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.register.RegisterViewModelFactory
import kotlinx.android.synthetic.main.fragment_description.*
import kotlinx.android.synthetic.main.fragment_forget_password.*
import kotlinx.android.synthetic.main.login_fragment.*

class ForgetPasswordFragment() : Fragment() {

    private var mViewModel: RegisterViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_forget_password, container, false)
        mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(RegisterViewModel::class.java)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        send.setOnClickListener {
            if (mail.text.toString().isNotEmpty())
                mViewModel?.forgetPassword(mail.text.toString())
            else
                mail.setError(getText(R.string.complete))
        }

        mViewModel?.forgetResponseMutableLiveData?.observe(this, Observer {
            if (it?.data?.state=="200")
                Toast.makeText(context,it.data.data,Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,getText(R.string.error_tryagani),Toast.LENGTH_LONG).show();
        })

        mViewModel?.errorinRegister?.observe(this, Observer {
                Toast.makeText(context,getText(R.string.error_tryagani),Toast.LENGTH_LONG).show();
        })


    }

    private fun getViewModelFactory(): ViewModelProvider.Factory {
        return RegisterViewModelFactory(activity!!.application)
    }
}
