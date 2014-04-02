package cn.edu.zju.isst.dao;

import cn.edu.zju.isst.common.PaginationList;
import cn.edu.zju.isst.entity.Job;

public interface JobDao extends Dao<Job> {
    public PaginationList<Job> findAll(int categoryId, String keywords, int status, int pageSize, int page);
}