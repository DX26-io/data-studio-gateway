package com.flair.bi.repository;

import com.flair.bi.domain.value.QValue;
import com.flair.bi.domain.value.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface ValueRepository extends JpaRepository<Value, Long>,
        QuerydslPredicateExecutor<Value>, QuerydslBinderCustomizer<QValue> {

    @Override
    default void customize(QuerydslBindings bindings, QValue root) {

    }
}
