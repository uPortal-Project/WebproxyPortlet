 
# Web Proxy Portlet Road Map

Dedicated to gathering requirements and general design for a refactoring of the web proxy portlet.

## Requirements
  - Authentication
    - URL to make auth request (GET|POST|PUT|...)
    - NTLM, Basic, Form
    - Session Timeout (allow re-auth after certain time idle or time since auth)
    - Extra fields for form
    - Which fields should the user be prompted for and if they are sensitive or not
    - Which prompted fields can be saved by user request or will be saved
    - EDIT mode allows modifying saved fields
  - URL Re-Writing
    - RegEx to define which URLs are proxied
    - RegEx to define which proxied URLs are binary and which are html (others are auto-determined)
    - Auto-determine html vs binary proxied URLs via content/type via HTTP HEAD
    - RegEx to define which URLs are proxied with an fname link
  - Caching
    - Cache content from remote site (pre processing)
    - Cache post processed content
    - Use expired on remote content failure
    - How frequently a failed site should be retried
  - Session Management
    - Store cookies (follow cookie rules|store all cookies)
    - Allow for explicit cookies to be defined
    - Allow for explicit headers to be defined
  - Navigation Bar
    - Location (top|bottom)
    - Options; back,forward,home,refresh,edit fields
  - HTTP Client
    - Connection Timeout
    - Read Timeout
    - Max Redirect Count
  - Clipping
    - Basic path based clipping (ex: /html/body/div3/span), allow multiple paths
    - Comment based clipping, allow multiple comments
  - HTML parsing options
    - Specific to parser used
    - Should generate SAX events but not 'fix' the content by default
  - Misc
    - URL Security
      - RegEx checking validates proxied URLs match configured proxy RegExs
      - Page URL tracking tracks URLs that have been rendered as proxiable
    - Content processing should be stream based (content is never buffered other than for caching)
      - HTTP Client should return the remote content as a stream
      - HTML to SAX event parsing should be stream based and not validate the content stream
      - URL re-writing, clipping, caching should all be SAX event or stream based
    - Shared Code
      - Many operations need to be available both in a portlet and servlet so work needs to be done to avoid code duplication
