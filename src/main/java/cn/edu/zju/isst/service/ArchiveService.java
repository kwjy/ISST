package cn.edu.zju.isst.service;

import java.util.List;

import cn.edu.zju.isst.entity.Archive;

public interface ArchiveService {
    public Archive find(int id);
    public List<Archive> findAll(String categoryAlias, int pageSize, int page);
}