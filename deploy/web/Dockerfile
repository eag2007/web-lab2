FROM httpd:2.4-alpine
COPY deploy/web/httpd.conf /usr/local/apache2/conf/httpd.conf
COPY frontend/index.html /usr/local/apache2/htdocs/index.html
COPY frontend/src /usr/local/apache2/htdocs/src
COPY frontend/public /usr/local/apache2/htdocs/public
