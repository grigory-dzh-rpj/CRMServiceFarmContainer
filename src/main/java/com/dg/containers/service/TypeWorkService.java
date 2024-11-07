package com.dg.containers.service;

import com.dg.containers.entity.work.TypeWork;
import com.dg.containers.repository.work.TypeWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypeWorkService {


    @Autowired
    private TypeWorkRepository typeWorkRepository;

    public TypeWork createTypeWork(TypeWork typeWork) {
        return typeWorkRepository.save(typeWork);
    }

    public TypeWork findByName(String nameWork){
        TypeWork typeWork = typeWorkRepository.findByNameWork(nameWork)
                .orElseThrow(() -> new EntityNotFoundException("TypeWork not found"));

        return typeWork;
    }

    @Transactional
    public void deleteTypeWorkByNameWork(String nameWork){
        typeWorkRepository.delete(findByName(nameWork));
    }


    public List<String> findAllTypeWorkNames(){
        return typeWorkRepository.findAll().stream().map(name -> name.getNameWork()).collect(Collectors.toList());
    }



}
