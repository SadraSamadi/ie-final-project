package com.sadrasamadi.iefinalproject.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.root}")
    public String root;

    public String save(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(filename);
            UUID uuid = UUID.randomUUID();
            String name = uuid + "." + extension;
            File dest = new File(root, name);
            InputStream inputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, dest);
            return "/resource/" + name;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Resource load(String name) {
        try {
            Path path = Paths.get(root, name);
            URI uri = path.toUri();
            return new UrlResource(uri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String name) {
        if (name.startsWith("/resource/")) {
            name = name.replace("/resource/", "");
            File file = new File(root, name);
            FileUtils.deleteQuietly(file);
        }
    }

}
