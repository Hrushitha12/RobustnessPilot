import argparse
import glob
import os
import xml.etree.ElementTree as ET

def parse_surefire_xml(report_path: str):
    """
    Extract test case names from surefire XML report.
    Returns list of "ClassName#testMethod".
    """
    tree = ET.parse(report_path)
    root = tree.getroot()  # <testsuite>
    classname = root.attrib.get("name")  # usually fully qualified class name
    test_ids = []
    for tc in root.findall("testcase"):
        method = tc.attrib.get("name")
        if classname and method:
            test_ids.append(f"{classname}#{method}")
    return test_ids

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--project_dir", required=True, help="Path to Maven project (contains pom.xml)")
    ap.add_argument("--pattern", default="TEST-*.xml", help="Surefire XML glob pattern (default: TEST-*.xml)")
    ap.add_argument("--out", required=True, help="Output file path (one test_id per line)")
    args = ap.parse_args()

    reports_dir = os.path.join(args.project_dir, "target", "surefire-reports")
    xml_glob = os.path.join(reports_dir, args.pattern)

    xml_files = glob.glob(xml_glob)
    if not xml_files:
        raise SystemExit(f"No surefire XML reports found at: {xml_glob}\n"
                         f"Run mvn test once to generate reports.")

    all_ids = []
    for xf in xml_files:
        all_ids.extend(parse_surefire_xml(xf))

    # de-duplicate while preserving order
    seen = set()
    uniq = []
    for tid in all_ids:
        if tid not in seen:
            uniq.append(tid)
            seen.add(tid)

    os.makedirs(os.path.dirname(args.out), exist_ok=True)
    with open(args.out, "w", encoding="utf-8") as f:
        for tid in uniq:
            f.write(tid + "\n")

    print(f"Wrote {len(uniq)} test ids to {args.out}")

if __name__ == "__main__":
    main()
