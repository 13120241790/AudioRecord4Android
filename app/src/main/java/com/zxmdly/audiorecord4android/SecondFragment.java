package com.zxmdly.audiorecord4android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.zxmdly.audiorecord4android.databinding.FragmentSecondBinding;
import com.zxmdly.record4android.AudioRecordManager;
import com.zxmdly.record4android.OnRecorderListener;

public class SecondFragment extends Fragment implements OnRecorderListener {

  private FragmentSecondBinding binding;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState
  ) {
//    AudioRecordManager.getInstance().addSubscribe(this);
    AudioRecordManager.getInstance().pauseProduce();
    binding = FragmentSecondBinding.inflate(inflater, container, false);
    return binding.getRoot();

  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        NavHostFragment.findNavController(SecondFragment.this)
            .navigate(R.id.action_SecondFragment_to_FirstFragment);
      }
    });
  }

  @Override
  public void onDestroyView() {
    AudioRecordManager.getInstance().removeSubscribe(this);
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onDispatch(byte[] bytes) {
    Log.e("zxm","onDispatch SecondFragment bytes ：" + bytes.length + " obj " + bytes.toString());
  }
}