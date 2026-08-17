package com.energie.platform.service;
import com.energie.platform.dto.Dtos.*; import com.energie.platform.exception.ApiException; import com.energie.platform.model.Region; import com.energie.platform.repository.Repositories.RegionRepository; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service @RequiredArgsConstructor public class RegionService {private final RegionRepository repo;
 @Transactional(readOnly=true) public List<RegionResponse> liste(){return repo.findAll().stream().map(Mapping::region).toList();}
 @Transactional(readOnly=true) public Region get(Long id){return repo.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Région introuvable"));}
 @Transactional public RegionResponse modifier(Long id,RegionUpdateRequest r){Region x=get(id);x.setLois(r.lois());x.setPrimes(r.primes());x.setChampsFormulaire(r.champsFormulaire());return Mapping.region(repo.save(x));}
}
