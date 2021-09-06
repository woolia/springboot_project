package project.pr.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.pr.domain.Member;

import java.util.ArrayList;
import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Menu {

    private String name;
    private Country country;
    private String displayName;

}
