package com.flair.bi.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report about which permissions does user have or does not have
 *
 * @param <T> permission grantee
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardGranteePermissionReport<T extends PermissionGrantee> {

    private T grantee;

    private Map<String, Object> info = new HashMap<>();

    private List<GranteePermissionReport<T>> views;
}
