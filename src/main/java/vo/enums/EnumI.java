package vo.enums;

public interface EnumI {
    Enum<?> findEByName(String name);

    Enum<?> findEByVal(int val);

}
