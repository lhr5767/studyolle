package com.studyolle.tag;

import com.studyolle.domain.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag,Long> {

    Tag findByTagTitle(String title);
}
