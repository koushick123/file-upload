package com.media_upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.media_upload.domain.FileTable;

@Repository
public interface FileUploadRepository extends JpaRepository<FileTable, Integer>{

}
