import requests
import argparse
from urllib.parse import urlparse

def add_https_if_missing(url):
    parsed_url = urlparse(url)
    if parsed_url.scheme == '':
        return "https://" + url
    else:
        return url

def check_csp(url):
    try:
        url_with_https = add_https_if_missing(url)
        response = requests.get(url_with_https)
        csp_header = response.headers.get('Content-Security-Policy')
        if csp_header:
            print("Present CSP")
            print(f"URL: {url_with_https} - {csp_header}")
            return True
        else:
            print("Missing CSP")
            print(f"URL: {url_with_https}")
            return False
    except requests.RequestException as e:
        print(f"Error fetching URL: {url_with_https} - {e}")
        return False

def main():
    parser = argparse.ArgumentParser(description="Check Content-Security-Policy header of URLs")
    parser.add_argument("-u", "--url", help="Single URL to check")
    parser.add_argument("-l", "--list", help="Path to a text file containing a list of URLs to check")
    args = parser.parse_args()

    found_csp = False

    if args.url:
        found_csp = check_csp(args.url)
    elif args.list:
        with open(args.list, "r") as file:
            urls = file.readlines()
            for url in urls:
                if check_csp(url.strip()):
                    found_csp = True
    else:
        print("Please provide either a single URL using -u or a list of URLs using -l")

if __name__ == "__main__":
    main()
