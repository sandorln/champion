package com.sandorln.champion.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.sandorln.champion.R
import com.sandorln.champion.databinding.CustomSearchBarBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

class SearchBar : FrameLayout {
    lateinit var binding: CustomSearchBarBinding

    private var hintText: String = ""
        set(value) {
            binding.inputLayoutSearch.hint = value
            field = value
        }

    constructor(context: Context) : super(context) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews(context, attrs, defStyleAttr)
    }

    private fun initViews(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        binding = CustomSearchBarBinding.inflate(LayoutInflater.from(context), null, false)
        attrs?.let {
            val attrsValue = context.obtainStyledAttributes(it, R.styleable.SearchBar, defStyleAttr, 0)
            hintText = attrsValue.getString(R.styleable.SearchBar_searchBarHint)?.uppercase() ?: ""
            attrsValue.recycle()
        }
        binding.imgCancel.setOnClickListener { binding.editSearch.setText("") }
        addView(binding.root)
    }

    val inputTextFlow = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val inputText = text.toString()
                trySendBlocking(inputText)

                binding.imgCancel.isVisible = inputText.isNotEmpty()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.editSearch.addTextChangedListener(textWatcher)
        awaitClose { binding.editSearch.removeTextChangedListener(textWatcher) }
    }.onStart {
        binding.imgCancel.isVisible = binding.editSearch.text.toString().isNotEmpty()
    }
}