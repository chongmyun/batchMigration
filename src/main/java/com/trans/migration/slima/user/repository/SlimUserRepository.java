package com.trans.migration.slima.user.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trans.migration.slima.user.domain.SlimUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.trans.migration.slima.user.domain.QSlimUser.slimUser;

@Repository
@RequiredArgsConstructor
public class SlimUserRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<SlimUser> getSlimUsersByPaging(long start,long end,int limit){
        List<SlimUser> slimUsers = jpaQueryFactory.selectFrom(slimUser).where(isGreaterTempKey(start),isLessOrEqualTempKey(end))
                .orderBy(slimUser.tempUserKey.asc()).limit(limit).fetch();

        return slimUsers;
    }

    private BooleanExpression isLessOrEqualTempKey(long end) {
        return end > 0 ? slimUser.tempUserKey.loe(end) : null;
    }

    private BooleanExpression isGreaterTempKey(long start){
        return start > 0 ? slimUser.tempUserKey.gt(start - 1) : null;
    }


}
