import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Makes it easier to manage the collection of businesses here
public class BusinessManager {
    private List<Business> allBusinesses = new ArrayList<>();

    public void addBusiness(Business b) {
        allBusinesses.add(b);
    }

    // Filters the list of businesses by a specific category
    public List<Business> getBusinessesByCategory(String category) {
        return allBusinesses.stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // Returns the full list of businesses by rating, in descending order.
    public List<Business> getTopRated() {
        return allBusinesses.stream()
                .sorted(Comparator.comparingDouble(Business::getRating).reversed())
                .collect(Collectors.toList());
    }
}