import requests
import argparse
from urllib.parse import urlparse

def add_https_if_missing(url):
    parsed_url = urlparse(url)
    if parsed_url.scheme == '':
        return "https://" + url
    else:
        return url

def check_csp(url, present_counter, missing_counter):
    try:
        url_with_https = add_https_if_missing(url)
        response = requests.get(url_with_https)
        csp_header = response.headers.get('Content-Security-Policy')
        if csp_header:
            print(f"URL: {url_with_https} - Content-Security-Policy Header: {csp_header}")
            present_counter += 1
        else:
            print(f"URL: {url_with_https} - Content-Security-Policy Header is missing")
            missing_counter += 1
    except requests.RequestException as e:
        print(f"Error fetching URL: {url_with_https} - {e}")
        missing_counter += 1

    return present_counter, missing_counter

def main():
    parser = argparse.ArgumentParser(description="Check Content-Security-Policy header of URLs")
    parser.add_argument("-u", "--url", help="Single URL to check")
    parser.add_argument("-l", "--list", help="Path to a text file containing a list of URLs to check")
    args = parser.parse_args()

    present_counter = 0
    missing_counter = 0

    if args.url:
        present_counter, missing_counter = check_csp(args.url, present_counter, missing_counter)
    elif args.list:
        with open(args.list, "r") as file:
            urls = file.readlines()
            for url in urls:
                present_counter, missing_counter = check_csp(url.strip(), present_counter, missing_counter)
    else:
        print("Please provide either a single URL using -u or a list of URLs using -l")

    print(f"Total URLs with Content-Security-Policy header: {present_counter}")
    print(f"Total URLs with missing Content-Security-Policy header: {missing_counter}")

if __name__ == "__main__":
    main()
