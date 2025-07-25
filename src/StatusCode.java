public enum StatusCode {
    OK("200 OK"),
    Created("201 Created"),
    Accepted("202 Accepted"),
    NoContent("204 No Content"),
    NotFound("404 Not Found"),
    BadRequest("400 Bad Request"),
    AprilFools("418 I’m a teapot (RFC 2324)");

    private final String code;

    StatusCode(String code) {
        this.code = code;
    }

    public String getStatusCode() {
        return code;
    }
}
