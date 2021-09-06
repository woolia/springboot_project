package project.pr.domain.status;


public enum ItemType {

    HAMBURGER("햄버거"), PIZZA("피자") , CHICKEN("치킨");

    private final String description;

    ItemType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
