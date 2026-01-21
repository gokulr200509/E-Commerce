import re
import sys
from urllib.parse import urljoin

import requests


URLS = [
    "https://www.tractorjunction.com/",
    "https://www.khetigaadi.com/",
    "https://www.agrostar.in/",
    "https://www.indiamart.com/agriculture-machinery/",
    "https://www.tradeindia.com/agriculture-machinery.html",
    "https://www.mahindratractor.com/",
    "https://www.tafe.com/",
    "https://www.sonalika.com/",
    "https://www.deere.co.in/",
    "https://www.vsttillers.com/",
    "https://www.agcocorp.com/",
    "https://www.cnhindustrial.com/",
    "https://www.kubota.com/",
    "https://www.claas.com/",
    "https://www.agriexpo.online/",
    "https://www.farmequip.org/",
    "https://www.machinerypete.com/",
    "https://www.alibaba.com/agriculture-machinery",
    "https://www.made-in-china.com/agriculture-machinery/",
    "https://www.fao.org/home/en",
]


def extract_meta(html: str, prop: str) -> str | None:
    # Match both property="og:image" and name="twitter:image" styles
    patterns = [
        rf'<meta[^>]+property=["\']{re.escape(prop)}["\'][^>]+content=["\']([^"\']+)["\']',
        rf'<meta[^>]+content=["\']([^"\']+)["\'][^>]+property=["\']{re.escape(prop)}["\']',
        rf'<meta[^>]+name=["\']{re.escape(prop)}["\'][^>]+content=["\']([^"\']+)["\']',
        rf'<meta[^>]+content=["\']([^"\']+)["\'][^>]+name=["\']{re.escape(prop)}["\']',
    ]
    for p in patterns:
        m = re.search(p, html, flags=re.IGNORECASE)
        if m:
            return m.group(1).strip()
    return None


def extract_title(html: str) -> str | None:
    m = re.search(r"<title[^>]*>(.*?)</title>", html, flags=re.IGNORECASE | re.DOTALL)
    if not m:
        return None
    title = re.sub(r"\s+", " ", m.group(1)).strip()
    return title or None


def main() -> int:
    session = requests.Session()
    session.headers.update(
        {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        }
    )

    rows = []
    for url in URLS:
        try:
            r = session.get(url, timeout=25, allow_redirects=True)
            final_url = r.url
            html = r.text
            og_image = extract_meta(html, "og:image")
            twitter_image = extract_meta(html, "twitter:image")
            title = extract_title(html)

            img = og_image or twitter_image
            if img:
                img = urljoin(final_url, img)
            rows.append((url, img, title, r.status_code, final_url))
        except Exception as e:
            rows.append((url, None, None, "ERR", str(e)))

    # Print in the user's desired format
    for (url, img, title, status, final_url) in rows:
        print(f"- Link: {url}")
        print(f"  - Image URL: {img or 'NOT_FOUND'}")
        print(f"  - Image description: {title or 'No title found'}")
        print(f"  - Debug: status={status}, final={final_url}")
        print()

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

