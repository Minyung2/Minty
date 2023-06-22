package com.Reboot.Minty.job.repository;

import com.Reboot.Minty.job.dto.JobSearchDto;
import com.Reboot.Minty.job.entity.Job;
import com.Reboot.Minty.job.entity.QJob;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobCustomRepository  {
    private final JPAQueryFactory queryFactory;

    public JobCustomRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<Job> findJobsBySearchDto(JobSearchDto jobSearchDto, Pageable pageable) {
        QJob qJob = QJob.job;
        BooleanExpression searchPredicate = createSearchPredicate(jobSearchDto);

        long total = queryFactory.selectFrom(qJob)
                .where(searchPredicate)
                .fetchCount();

        List<Job> jobs = queryFactory.selectFrom(qJob)
                .where(searchPredicate)
                .orderBy(qJob.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(jobs, pageable, total);
    }


    private BooleanExpression createSearchPredicate(JobSearchDto jobSearchDto) {
        QJob qJob = QJob.job;
        BooleanBuilder predicate = new BooleanBuilder();

        if (jobSearchDto.getSearchBy() != null && !jobSearchDto.getSearchBy().isEmpty()) {
            String searchQuery = jobSearchDto.getSearchQuery();

            if (searchQuery != null && !searchQuery.isEmpty()) { // Add this line
                if (jobSearchDto.getSearchBy().equals("content")) {
                    predicate.and(qJob.content.containsIgnoreCase(searchQuery));
                } else if (jobSearchDto.getSearchBy().equals("title")) {
                    predicate.and(qJob.title.containsIgnoreCase(searchQuery));
                } else if (jobSearchDto.getSearchBy().equals("jobLocation")) {
                    predicate.and(qJob.jobLocation.containsIgnoreCase(searchQuery));
                }
            }
        }

        return (BooleanExpression) predicate.getValue();
    }

}
