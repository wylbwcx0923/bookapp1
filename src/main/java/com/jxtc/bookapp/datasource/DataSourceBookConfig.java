package com.jxtc.bookapp.datasource;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author 不忘初心
 * SpringBoot多数据源的配置
 */
@Configuration
@MapperScan(basePackages = "com.jxtc.bookapp.mapper.book", sqlSessionTemplateRef = "dbBookSqlSessionTemplate")
public class DataSourceBookConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.dbbook")
    public DataSource dbBookDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory dbBookSqlSessionFactory(@Qualifier("dbBookDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapping/book/*.xml"));
        return bean.getObject();
    }

    @Bean
    public DataSourceTransactionManager dbBookTransactionManager(@Qualifier("dbBookDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionTemplate dbBookSqlSessionTemplate(@Qualifier("dbBookSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
