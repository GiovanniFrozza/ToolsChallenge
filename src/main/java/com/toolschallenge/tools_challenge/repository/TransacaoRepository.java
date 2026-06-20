package com.toolschallenge.tools_challenge.repository;

import com.toolschallenge.tools_challenge.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Optional<Transacao> findById(String id);

    boolean existsById(String id);
}
