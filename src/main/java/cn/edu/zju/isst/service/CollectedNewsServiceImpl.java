package cn.edu.zju.isst.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.zju.isst.collection.CSTCollectionSource;
import cn.edu.zju.isst.collection.CollectionEntity;
import cn.edu.zju.isst.dao.ArchiveDao;
import cn.edu.zju.isst.dao.CategoryDao;
import cn.edu.zju.isst.dao.CollectedNewsDao;
import cn.edu.zju.isst.entity.Archive;
import cn.edu.zju.isst.entity.Category;
import cn.edu.zju.isst.entity.CollectedNews;

@Service
public class CollectedNewsServiceImpl implements CollectedNewsService {
    @Autowired
    private CollectedNewsDao collectedNewsDao;
    @Autowired
    private ArchiveDao archiveDao;
    @Autowired
    private CategoryDao categoryDao;
    
    @Override
    public void collectAll() {
        CSTCollectionSource source = new CSTCollectionSource(new String[]{
            "实习就业", "重要通知", "学生思政", "新闻中心", "教务信息", "合作科研", "招生信息"
        });
        
        List<CollectionEntity> entities = source.collectList();
        Date createdAt = new Date();
        for (CollectionEntity entity : entities) {
            if (!collectedNewsDao.hasCollected(entity.getId())) {
                source.parseDetail(entity);
                CollectedNews collectedNews = new CollectedNews(entity);
                collectedNews.setCreatedAt(createdAt);
                collectedNewsDao.insert(collectedNews);
            }
        }
    }

    @Override
    public void publishAll() {
        Category category = categoryDao.find("campus");
        
        for (CollectedNews entity : collectedNewsDao.findAllUnpublished()) {
            Archive archive = new Archive();
            archive.setTitle(entity.getTitle());
            archive.setContent(entity.getContent());
            archive.setUpdatedAt(entity.getPostTime());
            archive.setStatus(Archive.STATUS_PUBLISHED);
            archive.setDescriptionFromContent(entity.getContent());
            archive.setCategoryId(category.getId());
            archiveDao.insert(archive);
            
            entity.setArchiveId(archive.getId());
            collectedNewsDao.update(entity);
        }
    }
}