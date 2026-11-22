package com.example.myapplication.lab6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.lab6.model.FileStorage
import com.example.myapplication.lab6.model.Profile

/**
 * 实验六第 1 个 Fragment：个人信息。
 * 编辑后点击保存，把 Profile 对象序列化写入私有文件；
 * 再次进入或退出程序重开时自动反序列化恢复历史信息。
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profile: Profile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvProfileTitle.setText(R.string.lab6_profile_title)

        // 反序列化恢复历史信息；没有存档时使用默认姓名学号
        profile = FileStorage.load(requireContext(), LabSixActivity.PROFILE_FILE, Profile::class.java)
            ?: Profile(
                name = getString(R.string.p_default_name),
                gender = getString(R.string.p_default_gender),
                studentId = getString(R.string.p_default_student_id),
                phone = getString(R.string.p_default_phone)
            )
        fillForm(profile)

        binding.btnSaveProfile.setOnClickListener {
            if (binding.etName.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), R.string.lab6_name_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            profile.name = binding.etName.text.toString().trim()
            profile.gender = binding.etGender.text.toString().trim()
            profile.studentId = binding.etStudentId.text.toString().trim()
            profile.phone = binding.etPhone.text.toString().trim()
            FileStorage.save(requireContext(), LabSixActivity.PROFILE_FILE, profile)
            refreshBestScore(profile)
            Toast.makeText(requireContext(), R.string.lab6_saved, Toast.LENGTH_SHORT).show()
        }
        refreshBestScore(profile)
    }

    private fun fillForm(p: Profile) {
        binding.etName.setText(p.name)
        binding.etGender.setText(p.gender)
        binding.etStudentId.setText(p.studentId)
        binding.etPhone.setText(p.phone)
    }

    private fun refreshBestScore(p: Profile) {
        binding.tvBestScore.text = getString(R.string.lab6_best_score, p.bestTwoScore, p.bestThreeScore)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
