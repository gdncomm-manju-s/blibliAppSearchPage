//package com.example.bliblihomepage.ui.productListPage
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.bliblihomepage.databinding.BottomSheetAddCartBinding
//import com.example.bliblihomepage.model.Product
//import com.example.bliblihomepage.util.Utils
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ProductAddBottomSheet(
//    private val productJson: String,
//    private val addAction: (Product) -> Unit
//) : BottomSheetDialogFragment() {
//
//    private var _binding: BottomSheetAddCartBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = BottomSheetAddCartBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val product = Utils.jsonToProduct(productJson)
//        if (product != null) {
//            binding.tvBsTitle.text = product.name
//            binding.tvBsPrice.text = product.price.priceDisplay ?: ""
//            binding.tvBsDesc.text = product.brand ?: ""
//        }
//
//        binding.btnBsAddCart.setOnClickListener {
//            if (product != null) {
//                addAction(product)
//            }
//            dismiss()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
