import requests
from datetime import datetime, timedelta
import matplotlib.pyplot as plt

# URL of the Wikimedia API for pageviews
base_url = "https://wikimedia.org/api/rest_v1/metrics/pageviews/top/cs.wikipedia/all-access/"

# Start and end dates for the range (360 days)
start_date = datetime(2023, 1, 1)
end_date = start_date + timedelta(days=360)

# Initialize lists to store days and corresponding set sizes
days = []
set_sizes = []

# Initialize an empty set to store article names
article_names_set = set()

# Iterate through each day in the specified range
current_date = start_date
while current_date <= end_date:
    # Format the current date
    formatted_date = current_date.strftime("%Y/%m/%d")

    # Construct the URL for the current date
    url = f"{base_url}{formatted_date}"

    # Make a GET request to the API with headers
    response = requests.get(url, headers={'User-Agent': 'Mozilla/5.0'})

    # Check if the request was successful (status code 200)
    if response.status_code == 200:
        # Parse the JSON response
        data = response.json()

        # Extract article names from the response
        articles = data['items'][0]['articles']
        article_names_set.update([item['article'] for item in articles])

        # Store the number of days and set size every 30 days
        if (current_date - start_date).days % 60 == 0:
            days.append((current_date - start_date).days)
            set_sizes.append(len(article_names_set))

    # Move to the next day
    current_date += timedelta(days=1)

# Create a graph
plt.figure(figsize=(8, 5))
plt.plot(days, set_sizes)
plt.xlabel('Počet dnů od 1.1.2023')
plt.ylabel('Velikost množiny')
plt.grid(True)
plt.tight_layout()

# Save the graph as PNG
plt.savefig('page_views_graph.png')

# Show the graph
plt.show()
