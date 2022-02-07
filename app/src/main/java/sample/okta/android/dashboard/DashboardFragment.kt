/*
 * Copyright 2021-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.okta.android.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.okta.authfoundation.credential.TokenType
import sample.okta.android.R
import sample.okta.android.databinding.FragmentDashboardBinding
import sample.okta.android.databinding.RowDashboardClaimBinding
import sample.okta.android.util.BaseFragment
import sample.okta.android.util.inflateBinding

internal class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate
) {
    private val args: DashboardFragmentArgs by navArgs()

    private val viewModel by viewModels<DashboardViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(args.credentialMetadataKey) as T
            }
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.tokenLiveData.observe(viewLifecycleOwner) { token ->
            binding.tokenType.text = token.tokenType
            binding.expiresIn.text = token.expiresIn.toString()
            binding.accessToken.text = token.accessToken
            binding.refreshToken.text = token.refreshToken
            binding.idToken.text = token.idToken
            binding.scope.text = token.scope

            if (token.refreshToken == null) {
                binding.refreshAccessTokenButton.visibility = View.GONE
                binding.introspectRefreshTokenButton.visibility = View.GONE
                binding.revokeRefreshTokenButton.visibility = View.GONE
            }
            if (token.deviceSecret == null) {
                binding.tokenExchangeButton.visibility = View.GONE
            }
        }

        viewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            binding.claimsTitle.visibility = if (userInfo.isEmpty()) View.GONE else View.VISIBLE
            for (entry in userInfo) {
                val nestedBinding = binding.linearLayout.inflateBinding(RowDashboardClaimBinding::inflate)
                nestedBinding.textViewKey.text = entry.key
                nestedBinding.textViewValue.text = entry.value
                nestedBinding.textViewValue.setTag(R.id.claim, entry.key)
                binding.claimsLinearLayout.addView(nestedBinding.root)
            }
        }

        binding.refreshAccessTokenButton.setOnClickListener {
            viewModel.refresh(binding.refreshAccessTokenButton.id)
        }

        binding.introspectAccessTokenButton.setOnClickListener {
            viewModel.introspect(binding.introspectAccessTokenButton.id, TokenType.ACCESS_TOKEN)
        }
        binding.introspectRefreshTokenButton.setOnClickListener {
            viewModel.introspect(binding.introspectRefreshTokenButton.id, TokenType.REFRESH_TOKEN)
        }
        binding.introspectIdTokenButton.setOnClickListener {
            viewModel.introspect(binding.introspectIdTokenButton.id, TokenType.ID_TOKEN)
        }

        binding.revokeAccessTokenButton.setOnClickListener {
            viewModel.revoke(binding.revokeAccessTokenButton.id, TokenType.ACCESS_TOKEN)
        }
        binding.revokeRefreshTokenButton.setOnClickListener {
            viewModel.revoke(binding.revokeRefreshTokenButton.id, TokenType.REFRESH_TOKEN)
        }
        binding.logoutWebButton.setOnClickListener {
            viewModel.logoutOfWeb(requireContext())
        }
        binding.removeCredentialButton.setOnClickListener {
            viewModel.removeCredential()
        }

        viewModel.requestStateLiveData.observe(viewLifecycleOwner) { state ->
            val button = binding.root.findViewById<View>(viewModel.lastButtonId)
            when (state) {
                DashboardViewModel.RequestState.Loading -> {
                    button?.isEnabled = false
                }
                is DashboardViewModel.RequestState.Result -> {
                    button?.isEnabled = true
                    binding.lastRequestInfo.text = state.text
                }
            }
        }

        binding.backToLogin.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.dashboardToLogin())
        }
        binding.tokenExchangeButton.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.dashboardToTokenExchange())
        }
    }
}
