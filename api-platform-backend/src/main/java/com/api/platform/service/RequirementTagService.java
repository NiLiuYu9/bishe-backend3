package com.api.platform.service;

import java.util.List;
import java.util.Map;

public interface RequirementTagService {

    List<String> getTagsByRequirementId(Long requirementId);

    void saveRequirementTags(Long requirementId, List<String> tags);

    Map<Long, List<String>> getTagsByRequirementIds(List<Long> requirementIds);

}
