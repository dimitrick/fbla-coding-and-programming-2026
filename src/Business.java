public class Business {
    private String name;
    private String category;
    private double rating;
    private int reviewCount;

    public Business(String name, String category, double rating) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.reviewCount = 1; // Default to 1 review for the initial data
    }

    // Mathematical logic for updating average rating
    public void addNewRating(int newStars) {
        double totalPoints = (this.rating * this.reviewCount) + newStars;
        this.reviewCount++;
        // Rounds to 1 decimal place
        this.rating = Math.round((totalPoints / this.reviewCount) * 10.0) / 10.0;
    }

    // Getters (Required by JavaFX TableView)
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
}