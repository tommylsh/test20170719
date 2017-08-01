import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class GenericClass<T extends String> {

	public static void main(String[] args) {
		for (TypeVariable typeParam : GenericClass.class.getTypeParameters()) {
			System.out.println(typeParam.getName());
			for (Type bound : typeParam.getBounds()) {
				System.out.println(bound);
			}
		}
	}
}