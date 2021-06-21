package com.flair.bi.service.search;

import com.flair.bi.compiler.search.SearchQLCompiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchQLManagerConfiguration {

    @Bean
    public SearchQLCompiler searchQLCompiler() {
        return new SearchQLCompiler();
    }
}
