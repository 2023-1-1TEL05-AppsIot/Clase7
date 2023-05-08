package com.example.clase7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;

import com.example.clase7.Retrofit.JobRepository;
import com.example.clase7.databinding.ActivityMainBinding;
import com.example.clase7.entity.Job;
import com.example.clase7.entity.JobDto;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "mainAct-test";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, "Carlos.txt");
            activityResultLauncher.launch(intent);
        });

        binding.button3.setOnClickListener(view -> {

        });

        JobRepository jobRepository = new Retrofit.Builder()
                .baseUrl("http://10.100.59.95:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(JobRepository.class);

        jobRepository.listarJobs().enqueue(new Callback<JobDto>() {
            @Override
            public void onResponse(Call<JobDto> call, Response<JobDto> response) {

                if (response.isSuccessful()) {
                    JobDto jobDto = response.body();
                    Log.d(TAG, "recepción correcta!");
                    Job[] jobs = jobDto.get_embedded().getJobs();
                    //guardarComoJson(jobs);
                    //guardarComoObjeto(jobs);
                    //listarTodosMisArchivos();
                    //leerArchivoDeText();
                    //leerArchivoComoObjeto();
                    //guardarComoObjetoEnSd(jobs);

                } else {
                    Log.d(TAG, "algo salió mal");
                }
            }

            @Override
            public void onFailure(Call<JobDto> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();

                    if (intent != null) {
                        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(intent.getData(), "w");
                             FileWriter fileWriter = new FileWriter(pfd.getFileDescriptor())) {

                            String textoGuardar = binding.editTextTextPersonName.getText().toString();
                            fileWriter.write(textoGuardar);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
    );


    public void guardarComoJson(Job[] listaJobs) {
        Gson gson = new Gson();
        String listaJobsAsJson = gson.toJson(listaJobs);

        Log.d(TAG, listaJobsAsJson);

        String fileName = "listaTrabajosComoJson";

        try (FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
             FileWriter fileWriter = new FileWriter(fileOutputStream.getFD())) {

            fileWriter.write(listaJobsAsJson);
            Log.d(TAG, "Guardado exitoso");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarComoObjeto(Job[] listaJobs) {

        String fileName = "listaTrabajosComoObjetos.niurka";

        try (FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            objectOutputStream.writeObject(listaJobs);
            Log.d(TAG, "Guardado exitoso");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listarTodosMisArchivos() {
        String[] fileList = fileList();

        for (String fileName : fileList) {
            Log.d(TAG, "file: " + fileName);
        }
    }

    public void leerArchivoDeText() {
        Log.d(TAG, "listado de archivo como texto");

        String fileName = "listaTrabajosComoJson";

        try (FileInputStream fileInputStream = openFileInput(fileName);
             FileReader fileReader = new FileReader(fileInputStream.getFD());
             BufferedReader bufferedReader = new BufferedReader(fileReader);) {

            String line = bufferedReader.readLine();
            Log.d(TAG, line);

            Gson gson = new Gson();
            Job[] jobs = gson.fromJson(line, Job[].class);

            for (Job j : jobs) {
                Log.d(TAG, "job: " + j.getJobTitle());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void leerArchivoComoObjeto() {
        Log.d(TAG, "listado de archivo como objeto");

        String fileName = "listaTrabajosComoObjetos.niurka";

        try (FileInputStream fileInputStream = openFileInput(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            Job[] jobs = (Job[]) objectInputStream.readObject();

            for (Job j : jobs) {
                Log.d(TAG, "job: " + j.getJobTitle());
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarComoObjetoEnSd(Job[] listaJobs) {

        String state = Environment.getExternalStorageState();
        boolean isSdPresent = Environment.MEDIA_MOUNTED.equals(state);

        if (isSdPresent) {
            String fileName = "listaTrabajosComoObjetos.jex";

            File fileSd = new File(getExternalFilesDir(null), fileName);

            try (FileOutputStream fileOutputStream = new FileOutputStream(fileSd);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

                objectOutputStream.writeObject(listaJobs);
                Log.d(TAG, "Guardado exitoso");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}