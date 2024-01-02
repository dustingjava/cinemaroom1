package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class Controller{
    List<Seats> seatsList = null;
    Map<String, SeatsTaken> seatsTaken = new ConcurrentHashMap<String, SeatsTaken>();
    @GetMapping("/seats")
    public ResponseEntity<Theatre> getSeats(){
        seatsList = new ArrayList<Seats>();
        Theatre theatre = new Theatre();

        theatre.setColumns(9);
        theatre.setRows(9);

        for(int i=1; i<=theatre.getRows(); i++) {
            for(int j=1; j<=theatre.getColumns(); j++) {
                Seats seats = new Seats();
                seats.setColumn(j);
                seats.setRow(i);
                if(i <= 4) {
                    seats.setPrice(10);
                } else {
                    seats.setPrice(8);
                }
                seatsList.add(seats);

            }
        }
        theatre.setSeats(seatsList);
        return new ResponseEntity<>(theatre, HttpStatus.OK);
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseSeats(@RequestBody ReqBody seats) {
        boolean seatPurchased = false;
        for(SeatsTaken seat : seatsTaken.values()) {
            if(seat.getTicket().getRow() == seats.getRow() && seat.getTicket().getColumn() == seats.getColumn()) {
                seatPurchased = true;
            }
        }
        if(seats.getRow() < 1 || seats.getRow() > 9 || seats.getColumn() > 9 || seats.getColumn() < 1) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ResBody("The number of a row or a column is out of bounds!"));
        } else if(seatPurchased) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ResBody("The ticket has been already purchased!"));
        } else {
            SeatsTaken newSeat = new SeatsTaken();
            newSeat.setTicket(seatsList.stream().filter(o -> o.getRow() == seats.getRow()
                    && o.getColumn() == seats.getColumn()).findFirst().get());
            newSeat.setToken(String.valueOf(UUID.randomUUID()));
            seatsTaken.put(newSeat.getToken(), newSeat);
            return new ResponseEntity<>(newSeat, HttpStatus.OK);
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnSeat(@RequestBody ReturnReq returnReq) {
        ReturnRes returnRes = new ReturnRes();

                if (null != seatsTaken.get(returnReq.getToken())) {
                    returnRes.setTicket(seatsTaken.get(returnReq.getToken()).getTicket());
                    seatsTaken.remove(returnReq.getToken());
                }


            if (null != returnRes.getTicket()) {
                return ResponseEntity.status(HttpStatus.OK).body(returnRes);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(new ResBody("Wrong token!"));
            }

    }
    @GetMapping("/stats")
    public ResponseEntity<?> stats(@RequestParam(value = "password", required = false) String password){
        int income = 0;
        int available = 81;
        int purchased = 0;
        if("super_secret".equalsIgnoreCase(password)){
            for(SeatsTaken seat: seatsTaken.values()){
                income = income + seat.getTicket().getPrice();
            }
            purchased = seatsTaken.values().size();
            available = available - purchased;
            return ResponseEntity.status(HttpStatus.OK).body(new StatsRes(income, available, purchased));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(new ResBody("The password is wrong!"));
        }
    }
}
