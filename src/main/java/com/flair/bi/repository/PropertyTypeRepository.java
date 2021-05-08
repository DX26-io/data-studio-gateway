package com.flair.bi.repository;

import com.flair.bi.domain.propertytype.PropertyType;
import com.flair.bi.domain.propertytype.QPropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface PropertyTypeRepository extends JpaRepository<PropertyType, Long>,
        QuerydslPredicateExecutor<PropertyType>, QuerydslBinderCustomizer<QPropertyType> {

    @Override
    default void customize(QuerydslBindings bindings, QPropertyType root) {

    }

}
