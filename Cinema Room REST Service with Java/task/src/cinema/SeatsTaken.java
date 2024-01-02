package cinema;

import java.util.Objects;

public class SeatsTaken {

    private String token;
    private Seats ticket;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Seats getTicket() {
        return ticket;
    }

    public void setTicket(Seats ticket) {
        this.ticket = ticket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seats class1 = (Seats) o;
        return Objects.equals(ticket, class1);
    }
}
