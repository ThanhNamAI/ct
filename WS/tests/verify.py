"""Compile and exercise all WS clients against a local SOAP stub (Python 3 + JDK)."""
from datetime import datetime, timedelta, timezone
from http.server import BaseHTTPRequestHandler, HTTPServer
from pathlib import Path
from tempfile import TemporaryDirectory
from threading import Thread
from xml.etree import ElementTree as ET
from xml.sax.saxutils import escape
import os
import re
import shutil
import subprocess


ROOT = Path(__file__).resolve().parents[1]
SOAP = 'http://schemas.xmlsoap.org/soap/envelope/'
TODAY = datetime.now(timezone(timedelta(hours=7))).date()


def object_xml(**fields):
    return ''.join(f'<o:{k}>{escape(str(v))}</o:{k}>' for k, v in fields.items())


def local(tag):
    return tag.rsplit('}', 1)[-1]


def date(days):
    return str(TODAY + timedelta(days=days))


# number, raw return values, submitted parameter, expected values, object/scalar
CASES = [
    (1411, ['1', '2', '-3', '4'], 'data', ['4'], False),
    (1412, ['2', '1', '7', '3', '9'], 'data', ['7', '3'], False),
    (1413, ['2', '1', '2', '-1'], 'data', ['-1,1', '1,1', '2,2'], False),
    (1414, ['2', '1', '2', '3', '1'], 'data', ['2', '1', '3'], False),
    (1415, ['1', '2', '3', '4', '5'], 'data', ['2', '1', '4', '3', '5'], False),
    (1416, ['0', '5', '10'], 'data', ['0', '101', '1010'], False),
    (1417, ['28', '13', '1'], 'data', ['2, 2, 7', '13', '1'], False),
    (1418, ['8', '15'], 'data', ['10|8', '17|f'], False),
    (1419, ['0.75'], 'data', ['3', '4'], False),
    (1420, ['12.69'], 'data', ['6', '1', '2', '3', '4', '6', '12'], False),
    (1421, ['99', '97', '98'], 'data', ['97', '98', '99'], False),
    (1422, ['65', '66', '67'], 'data', ['66', '67', '65'], False),
    (1423, ['hello WORLD_test'], 'data', ['HelloWorldTest', 'helloWorldTest', 'hello_world_test'], False),
    (1424, ['hello@ world! 2024'], 'cleanedAndReversedString', ['dlrowolleh'], False),
    (1425, ['a\nlongest bb'], 'data', ['longest; a'], False),
    (1426, ['a', 'longest', 'bb'], 'data', ['longest; a'], False),
    (1427, ['&amp;lt;', '&#65;', '<![CDATA[&&]]>', ''], 'data', ['', 'A', '&&', '&lt;'], False),
    (1428, ['pear', 'sky', 'apple', 'bee'], 'data', ['sky', 'apple', 'bee', 'pear'], False),
    (1429, ['pear', 'sky', 'apple', 'bee'], 'data', ['sky', 'pear', 'apple', 'bee'], False),
    (1431, [object_xml(id='P&1', price=100, taxRate=10, discount=5) + '<o:finalPrice xsi:nil="true"/>'],
     'object', [dict(id='P&1', price='100', taxRate='10', discount='5', finalPrice='104.5')], True),
    (1432, [object_xml(id='E1', startDate='2024-01-01', endDate='2024-01-07')],
     'object', [dict(id='E1', startDate='2024-01-01', endDate='2024-01-07', workingDays='5')], True),
    (1433, [object_xml(id='new', startDate='2024-01-01'), object_xml(id='old', startDate='2020-01-01')],
     'data', [dict(id='old', startDate='2020-01-01'), dict(id='new', startDate='2024-01-01')], True),
    (1434, [object_xml(id='A', score=8), object_xml(id='B', score=5), object_xml(id='D', score=4.9)],
     'students', [dict(id='A', score='8'), dict(id='D', score='4.9')], True),
    (1435, [object_xml(customerId='A', amount=10), object_xml(customerId='B', amount=30), object_xml(customerId='A', amount=25)],
     'data', [dict(customerId='A', amount='10'), dict(customerId='A', amount='25')], True),
    (1436, [object_xml(id='old', status='pending', orderDate=date(-31)), object_xml(id='edge', status='processing', orderDate=date(-30)), object_xml(id='done', status='done', orderDate=date(-90))],
     'orderYs', [dict(id='old', status='pending', orderDate=date(-31))], True),
    (1437, [object_xml(id='1', subject='Math', score=9), object_xml(id='2', subject='Math', score=8), object_xml(id='3', subject='CS', score=7), object_xml(id='4', subject='Math', score=9)],
     'studentYs', [dict(id='1', subject='Math', score='9'), dict(id='3', subject='CS', score='7'), dict(id='4', subject='Math', score='9')], True),
    (1438, [object_xml(id='yes', completionPercentage=80, dueDate=date(15)), object_xml(id='late', completionPercentage=90, dueDate=date(-1)), object_xml(id='far', completionPercentage=90, dueDate=date(16))],
     'projects', [dict(id='yes', completionPercentage='80', dueDate=date(15))], True),
    (1439, [object_xml(id='yes', totalSpent=5001, purchaseCount=5), object_xml(id='edge', totalSpent=5000, purchaseCount=5)],
     'customers', [dict(id='yes', totalSpent='5001', purchaseCount='5')], True),
    (1440, [object_xml(id='old', lastTransactionDate=date(-250)), object_xml(id='new', lastTransactionDate=date(-1)), object_xml(id='empty', lastTransactionDate=''), object_xml(id='nil') + '<o:lastTransactionDate xsi:nil="true"/>'],
     'customerYs', [dict(id='old', lastTransactionDate=date(-250)), dict(id='empty', lastTransactionDate=''), dict(id='nil', lastTransactionDate='')], True),
    (961, ['3', '30', '34', '5', '9'], 'data', ['9534330'], False),
    (1419, ['1.005'], 'data', ['101', '100'], False),
    (1419, ['-1.005'], 'data', ['-101', '100'], False),
    (1419, ['0.00'], 'data', ['0', '1'], False),
    (1420, ['2147483647.9'], 'data', ['2', '1', '2147483647'], False),
    (1422, [], 'data', [], False),
    (1425, [''], 'data', ['; '], False),
    (1426, [], 'data', ['; '], False),
    (961, ['12', '121'], 'data', ['12121'], False),
    (1411, ['2147483647', '1', '-1'], 'data', ['2147483647'], False),
    (1426, ['', 'hello', 'a'], 'data', ['hello; '], False),
]


class Stub(BaseHTTPRequestHandler):
    case = None
    error_mode = None
    submitted = []
    failures = []

    def log_message(self, *args):
        pass

    def reply(self, body, status=200):
        data = body.encode('utf-8')
        self.send_response(status)
        self.send_header('Content-Type', 'text/xml; charset=utf-8')
        self.send_header('Content-Length', str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def do_GET(self):
        if self.error_mode == 'bad-wsdl':
            self.reply('<definitions/>')
        else:
            # Single quotes and spaces are valid XML, and defeat the previous regex.
            self.reply("<?xml version='1.0'?><definitions targetNamespace = 'urn:ws-test'/>")

    def do_POST(self):
        try:
            request = ET.fromstring(self.rfile.read(int(self.headers['Content-Length'])))
            operation = request.find(f'{{{SOAP}}}Body')[0]
            assert operation.tag.startswith('{urn:ws-test}')
            assert self.headers['SOAPAction'] == '""'
            params = list(operation)
            assert [local(p.tag) for p in params[:2]] == ['studentCode', 'qCode']
            assert params[0].text == 'TEST-STUDENT'
            assert params[1].text == f'TEST-Q-{self.case[0]}'
            if not local(operation.tag).startswith('submit'):
                if self.error_mode in ('fault-200', 'fault-500'):
                    self.reply(f'<s:Envelope xmlns:s="{SOAP}"><s:Body><s:Fault><faultcode>s:Server</faultcode><faultstring>test fault</faultstring></s:Fault></s:Body></s:Envelope>', 500 if self.error_mode == 'fault-500' else 200)
                elif self.error_mode == 'empty-500':
                    self.reply('', 500)
                elif self.error_mode == 'malformed':
                    self.reply('<broken')
                else:
                    returns = ''.join(f'<ns-test:return>{v}</ns-test:return>' if v else '<ns-test:return/>' for v in self.case[1])
                    self.reply(f'<s:Envelope xmlns:s="{SOAP}" xmlns:ns-test="urn:ws-test" xmlns:o="urn:object" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><s:Body><ns-test:response>{returns}</ns-test:response></s:Body></s:Envelope>')
            else:
                self.submitted.append(operation)
                self.reply(f'<s:Envelope xmlns:s="{SOAP}"><s:Body/></s:Envelope>')
        except Exception as exc:
            self.failures.append(str(exc) or repr(exc))
            self.reply('invalid request', 500)


def java_tools():
    javac = shutil.which('javac')
    if not javac and os.environ.get('JAVA_HOME'):
        javac = str(Path(os.environ['JAVA_HOME']) / 'bin' / ('javac.exe' if os.name == 'nt' else 'javac'))
    if not javac:
        candidates = sorted((Path.home() / '.jdks').glob('*/bin/javac.exe'))
        if candidates:
            javac = str(candidates[-1])
    if not javac:
        raise SystemExit('JDK not found. Put javac on PATH or set JAVA_HOME.')
    return javac, str(Path(javac).with_name('java.exe' if os.name == 'nt' else 'java'))


def main():
    javac, java = java_tools()
    server = HTTPServer(('127.0.0.1', 0), Stub)
    thread = Thread(target=server.serve_forever, daemon=True)
    thread.start()
    try:
        with TemporaryDirectory(prefix='jnp_ws_verify_') as temp:
            temp = Path(temp)
            classes = {}
            sources = []
            for source in sorted(ROOT.glob('WS_*.java')):
                text = source.read_text(encoding='utf-8')
                number = int(source.stem.split('_')[1])
                classes[number] = source.stem
                text, hosts = re.subn(r'String\s+host\s*=\s*"[^"]+"\s*;', 'String host = "127.0.0.1";', text)
                text, ports = re.subn(r'int\s+port\s*=\s*\d+\s*;', f'int port = {server.server_port};', text)
                assert hosts == ports == 1, f'Cannot redirect {source.name} to the local stub'
                text = re.sub(r'String sCode = "[^"]+";', 'String sCode = "TEST-STUDENT";', text)
                text = re.sub(r'String qCode = "[^"]+";', f'String qCode = "TEST-Q-{number}";', text)
                target = temp / source.name
                target.write_text(text, encoding='utf-8')
                sources.append(str(target))
            assert set(classes) == {c[0] for c in CASES}, 'Every client must have a result case'
            subprocess.run([javac, '-encoding', 'UTF-8', '--release', '8', '-Xlint:-options', '-d', str(temp), *sources], check=True, capture_output=True)
            for case in CASES:
                Stub.case, Stub.error_mode = case, None
                Stub.submitted, Stub.failures = [], []
                run = subprocess.run([java, '-Duser.timezone=Asia/Ho_Chi_Minh', '-cp', str(temp), 'WS.' + classes[case[0]]], capture_output=True, text=True, encoding='utf-8', errors='replace', timeout=15)
                assert not Stub.failures, Stub.failures
                assert run.returncode == 0 and len(Stub.submitted) == 1, (case[0], run.stdout, run.stderr)
                params = list(Stub.submitted[0])[2:]
                assert all(local(p.tag) == case[2] for p in params), (case[0], [local(p.tag) for p in params])
                if case[4]:
                    actual = [{local(c.tag): c.text or '' for c in p} for p in params]
                    for p in params:
                        assert all(c.tag.startswith('{urn:object}') for c in p), (case[0], [c.tag for c in p])
                        assert not any(c.get('{http://www.w3.org/2001/XMLSchema-instance}nil') in ('true', '1') and c.text for c in p)
                else:
                    actual = [p.text or '' for p in params]
                assert actual == case[3], (case[0], actual, case[3])
            for mode, message in [('bad-wsdl', 'targetNamespace'), ('fault-200', 'SOAP Fault'), ('fault-500', 'HTTP 500'), ('empty-500', 'HTTP 500'), ('malformed', 'SAXParseException')]:
                Stub.case, Stub.error_mode = CASES[0], mode
                Stub.submitted, Stub.failures = [], []
                run = subprocess.run([java, '-cp', str(temp), 'WS.' + classes[1411]], capture_output=True, text=True, encoding='utf-8', errors='replace', timeout=15)
                assert not Stub.failures and not Stub.submitted, (mode, Stub.failures)
                assert message in run.stderr, (mode, run.stderr)
            print(f'PASS: {len(classes)} clients compile for Java 8; {len(CASES)} result cases; 5 HTTP/XML error cases.')
    finally:
        server.shutdown()
        server.server_close()
        thread.join()


if __name__ == '__main__':
    main()
