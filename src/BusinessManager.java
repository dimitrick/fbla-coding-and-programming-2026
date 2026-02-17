import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessManager {
    private List<Business> allBusinesses = new ArrayList<>();

    public void addBusiness(Business b) {
        allBusinesses.add(b);
    }

    public List<Business> getBusinessesByCategory(String category) {
        return allBusinesses.stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Business> getTopRated() {
        return allBusinesses.stream()
                .sorted(Comparator.comparingDouble(Business::getRating).reversed())
                .collect(Collectors.toList());
    }
}