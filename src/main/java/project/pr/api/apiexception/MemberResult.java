package project.pr.api.apiexception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResult<T> {
    private T data;
}