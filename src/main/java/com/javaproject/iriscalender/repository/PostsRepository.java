package com.javaproject.iriscalender.repository;

import com.javaproject.iriscalender.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;


// DB Layer 접근자
public interface PostsRepository extends JpaRepository<Posts, Long>{
}
