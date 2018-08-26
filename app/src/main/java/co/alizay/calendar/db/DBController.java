package co.alizay.calendar.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import co.alizay.calendar.db.entity.CareGiverEntity;
import co.alizay.calendar.db.entity.OvertimeEntity;
import co.alizay.calendar.db.entity.WorkEntity;
import co.alizay.calendar.models.CareGiver;

public class DBController {

    public static class Room {
        int room;
        boolean isFree;
        Room(int room, boolean isFree) {
            this.room = room;
            this.isFree = isFree;
        }
    }

    static Map<Long, Room> roomAvailable = new HashMap<>();

    public static void generateData(AppDatabase mAppDatabase, String[] patients, List<CareGiver> careGivers) {
        int i = 1;
        int hour = 9;
        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(Calendar.MINUTE, 0);
        startTimeCalendar.set(Calendar.SECOND, 0);
        for (String patient: patients) {
            int day = startTimeCalendar.get(Calendar.DAY_OF_WEEK);
            if(hour <= 16 && day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
                startTimeCalendar.set(Calendar.HOUR_OF_DAY, hour);

                WorkEntity workEntity = new WorkEntity();

                workEntity.setPatientName(patient);
                workEntity.setRoom(i);
                workEntity.setStartTime(startTimeCalendar.getTimeInMillis());
                workEntity.setEndTime(startTimeCalendar.getTimeInMillis());

                addWork(mAppDatabase, workEntity, startTimeCalendar, careGivers.get(i - 1));
                i = i + 1;
                hour = hour + 1;
            } else {
                hour = 9;
                startTimeCalendar.set(Calendar.DAY_OF_MONTH, startTimeCalendar.get(Calendar.DAY_OF_MONTH) + 1);
            }
        }
        for (int j = 10; j < careGivers.size(); j ++) {
            addCareGiver(mAppDatabase, careGivers.get(j));
        }
        System.out.println("Total added: " + i);
    }

    public static long addCareGiver(AppDatabase mAppDatabase, CareGiver careGiver) {

        CareGiverEntity careGiverEntity = mAppDatabase.daoCareGiver()
                .loadCareGiverByEmail(careGiver.getEmail());
        long id;
        if (careGiverEntity != null) {
//            System.out.println(careGiverEntity.getEmail() + " careGiverEntity.getEmail()");
            id = careGiverEntity.getId();
        } else {
            careGiverEntity = new CareGiverEntity(careGiver);
            id = mAppDatabase.daoCareGiver().insert(careGiverEntity);
        }

        return id;
    }

    public static int isRoomFreeDB(AppDatabase mAppDatabase, int room, long atTime) {
        int isRoomFree = mAppDatabase.daoWork().isRoomFree(room, atTime);
        return isRoomFree;
    }

    public static boolean isRoomFree(AppDatabase mAppDatabase, int room, long atTime) {
        Room r = roomAvailable.get(atTime);
        if (r != null && r.room == room) {
            return r.isFree;
        }
        return true;
    }

    public static boolean isCareGiverAvailableAtTime(AppDatabase mAppDatabase, String email, long time) {
        WorkEntity work = mAppDatabase.daoWork().getWorkCareGiver(email, time);
        if(work != null) {
            return false;
        }
        OvertimeEntity overtimeEntity = mAppDatabase.daoOvertime().getOvertimeCareGiver(email, time);
        if(overtimeEntity != null) {
            return false;
        }
        return true;
    }

    public static String addWork(AppDatabase mAppDatabase, WorkEntity workEntity, Calendar selectedDate,
                                 CareGiver selectedCareGiver) {

        long[] time = getWeekStartAndEnd(selectedDate);

        boolean isRoomFree = isRoomFree(mAppDatabase, workEntity.getRoom(), selectedDate.getTimeInMillis());
        System.out.println("isRoomFree ================== " + isRoomFree + " at time " + time[0]);

        if (!isRoomFree) {
            return "RoomNotAvailable";
        } else {

            boolean isCareGiverAvailableAtTime = isCareGiverAvailableAtTime(mAppDatabase, selectedCareGiver.getEmail(), time[0]);

            if(!isCareGiverAvailableAtTime) {
                return "CareGiverNotAvailable";
            }

            long id = addCareGiver(mAppDatabase, selectedCareGiver);

            List<WorkEntity> entities = mAppDatabase.daoWork().loadWorkOfCareGiver(id,
                    time[0], time[1]);

            workEntity.setCareGiverId(id);

            if (entities.size() > 5) {
                return "SaveAsOvertime";
            } else {
                long idWEnt = mAppDatabase.daoWork().insert(workEntity);
                System.out.println(" ********************* : " + workEntity.toString());
                roomAvailable.put(selectedDate.getTimeInMillis(), new Room(workEntity.getRoom(), false));
                return "WorkAdded";
            }
        }
    }

    public static String addOvertime(AppDatabase mAppDatabase, WorkEntity workEntity,
                                     long startTime, long endTime) {
        List<OvertimeEntity> overtime = mAppDatabase.daoOvertime()
                .loadOvertimesByCaregiverId(workEntity.getCareGiverId(), startTime, endTime);
        if (overtime.size() > 1) {
            return "OvertimeFinished";
        } else {
            OvertimeEntity overtimeEntity = new OvertimeEntity();
            overtimeEntity.setCareGiverId(workEntity.getCareGiverId());
            overtimeEntity.setEndTime(workEntity.getEndTime());
            overtimeEntity.setStartTime(workEntity.getStartTime());
            overtimeEntity.setRoom(workEntity.getRoom());
            overtimeEntity.setPatientName(workEntity.getPatientName());
            mAppDatabase.daoOvertime().insert(overtimeEntity);
            roomAvailable.put(startTime, new Room(workEntity.getRoom(), false));
            return "OvertimeAdded";
        }
    }

    public static long[] getWeekStartAndEnd(Calendar selectedDate) {

        long[] time = new long[2];

        Calendar weekStart = (Calendar) selectedDate.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, selectedDate.getFirstDayOfWeek());
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);
        weekStart.set(Calendar.MILLISECOND, 0);

        time[0] = weekStart.getTimeInMillis();

        Calendar weekEnd = (Calendar) selectedDate.clone();
        int lastDay = weekEnd.getActualMaximum(Calendar.DAY_OF_WEEK);
        weekEnd.set(Calendar.DAY_OF_WEEK, lastDay);
        weekEnd.set(Calendar.HOUR_OF_DAY, 23);
        weekEnd.set(Calendar.MINUTE, 59);
        weekEnd.set(Calendar.SECOND, 59);
        weekEnd.set(Calendar.MILLISECOND, 999);

        time[1] = weekEnd.getTimeInMillis();
        return time;
    }

    public static long[] getDayStartAndEndWorkingHours(Calendar selectedDate) {

        long[] time = new long[2];

        Calendar workDayStart = (Calendar) selectedDate.clone();
        workDayStart.set(Calendar.HOUR_OF_DAY, 9);
        workDayStart.set(Calendar.MINUTE, 0);
        workDayStart.set(Calendar.SECOND, 0);
        workDayStart.set(Calendar.MILLISECOND, 0);

        time[0] = workDayStart.getTimeInMillis();

        Calendar workDayEnd = (Calendar) workDayStart.clone();
        workDayEnd.set(Calendar.HOUR_OF_DAY, 16);
        workDayEnd.set(Calendar.MINUTE, 59);
        workDayEnd.set(Calendar.SECOND, 59);
        workDayEnd.set(Calendar.MILLISECOND, 999);

        time[1] = workDayEnd.getTimeInMillis();
        return time;
    }

    public static List<Long> getWorkingTimeSlots() {
        List<Long> timeSlots = new ArrayList<>();

        Calendar workDayStart = Calendar.getInstance();
        workDayStart.set(Calendar.HOUR_OF_DAY, 9);
        workDayStart.set(Calendar.MINUTE, 0);
        workDayStart.set(Calendar.SECOND, 0);
        workDayStart.set(Calendar.MILLISECOND, 0);

        long startingHour = workDayStart.getTimeInMillis();

        Calendar workDayEnd = (Calendar) workDayStart.clone();
        workDayEnd.set(Calendar.DAY_OF_YEAR, workDayStart.get(Calendar.DAY_OF_YEAR) + 6);
        workDayEnd.set(Calendar.HOUR_OF_DAY, 16);
        workDayEnd.set(Calendar.MINUTE, 59);
        workDayEnd.set(Calendar.SECOND, 59);
        workDayEnd.set(Calendar.MILLISECOND, 999);

        long endingHour = workDayEnd.getTimeInMillis();

        Calendar nextHour = Calendar.getInstance();
        nextHour.setTimeInMillis(startingHour);
        nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);
        long t = nextHour.getTimeInMillis();

        timeSlots.add(startingHour);
        while (startingHour <= t && t <= endingHour) {
            nextHour.setTimeInMillis(t);
            int getHour = nextHour.get(Calendar.HOUR_OF_DAY);
            int day = nextHour.get(Calendar.DAY_OF_WEEK);
            if(getHour >= 9 && getHour <= 17 && day != Calendar.SATURDAY && day != Calendar.SUNDAY ) {
                timeSlots.add(t);
                //System.out.println(nextHour.getTime().toString());
            }
            nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);
            t = nextHour.getTimeInMillis();
        }

        return timeSlots;
    }

    public static void autoFillWithoutAnd(AppDatabase mAppDatabase) {
        /**
         * The auto-fill feature should follow the criteria below:
         * 1. If multiple caregivers are eligible,follow the priority scale below:
         *      1. A caregiver with no overtime.
         *      2. A caregiver already working the same day,in the nearest room.
         *      3. A caregiver that has worked less hours in the past4 weeks.
         * 2. If no caregivers are available,the slot will stay vacant.
         **/

        Calendar cal = Calendar.getInstance();
        long[] times = getWeekStartAndEnd(cal);

        List<CareGiverEntity> careGiverEntities = mAppDatabase.daoCareGiver().loadAll();

        List<Long> timeSlots = getWorkingTimeSlots();

        timeSlot: for(long timeSlot: timeSlots) {

            roomNo: for(int room = 1; room <= 10; room ++) {

                boolean isRoomFree = isRoomFree(mAppDatabase, room, timeSlot);
//                System.out.println(room + " isRoomFree: ================ " + isRoomFree + " at " + timeSlot);
                if (!isRoomFree) {
                    continue roomNo;
                }

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(timeSlot);

                long[] dayStartEnd = getDayStartAndEndWorkingHours(c);

                Map<Long, Integer> roomPref = new HashMap<>();

                careGiver: for (CareGiverEntity cg: careGiverEntities) {

                    boolean free = isRoomFree(mAppDatabase, room, timeSlot);
                    System.out.println(room + " free " + free + " at " + timeSlot);
                    if (!free) {
                        continue roomNo;
                    }

                    /**
                     * 1. A caregiver with no overtime.
                     **/
                    // checking overtime within one week
                    List<OvertimeEntity> overtime = mAppDatabase.daoOvertime()
                            .loadOvertimesByCaregiverId(cg.getId(), times[0], times[1]);
                    if (overtime.size() == 0) {
                        WorkEntity w = mAppDatabase.daoWork()
                                .CareGiverWorkAtTime(cg.getEmail(), dayStartEnd[0]);

                        if (w == null) {
                            WorkEntity workEntity = createWork(room, timeSlot, cg.getCareGiver());

                            Calendar selectedTime = Calendar.getInstance();
                            selectedTime.setTimeInMillis(timeSlot);
                            String result = addWork(mAppDatabase, workEntity, selectedTime, cg.getCareGiver());

                            System.out.println("1 ======= result: " + result);

                            if (result.equals("CareGiverNotAvailable")) {
                                continue careGiver;
                            } else if (result.equals("SaveAsOvertime")) {
                                Calendar endTime = Calendar.getInstance();
                                endTime.setTimeInMillis(timeSlot);
                                endTime.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY) + 1);

                                String res = DBController.addOvertime(mAppDatabase, workEntity, timeSlot, endTime.getTimeInMillis());

                                if (res.equals("OvertimeAdded")) {
                                    continue careGiver;
                                }
                            } else if (result.equals("WorkAdded")) {
                                continue careGiver;
                            } else if (result.equals("RoomNotAvailable")) {
                                continue roomNo;
                            }

                            /**
                             * 2. A caregiver already working the same day,in the nearest room.
                             **/
                            // checking the work of caregiver in the same day
                            List<WorkEntity> works = mAppDatabase.daoWork()
                                    .loadWorkOfCareGiver(cg.getId(), dayStartEnd[0], dayStartEnd[1]);

                            // find the nearest room of caregiver
                            int theNumber = findNearestRoom(works, room, timeSlot);
                            if (theNumber != 0) {
                                roomPref.put(cg.getId(), theNumber);
                            }
                        }
                    } else {
                        continue careGiver;
                    }
                }


//                if(roomPref != null) {
//                    long cId = findClosest(roomPref, room);
//                    CareGiverEntity cgEntity = mAppDatabase.daoCareGiver().loadCareGiver(cId);
//                    if(cgEntity != null) {
//                        WorkEntity workEntity = createWork(room, timeSlot, cgEntity.getCareGiver());
//                        Calendar startTime = Calendar.getInstance();
//                        startTime.setTimeInMillis(timeSlot);
//                        String result = addWork(mAppDatabase, workEntity, startTime, cgEntity.getCareGiver());
//                        System.out.println("2 ======= result: " + result);
//                        if (result.equals("WorkAdded")) {
//                            continue timeSlot;
//                        }
//                    }
//                }
            }

//            for(int room = 1; room <= 10; room ++) {
//
//                boolean isRoomFree = isRoomFree(mAppDatabase, room, timeSlot);
//                if (!isRoomFree) {
//
//                    /**
//                     * 3. A caregiver that has worked less hours in the past 4 weeks.
//                     **/
//                    Map<Long, Integer> careGiverPriorities = new HashMap<>();
//                    for (CareGiverEntity cg: careGiverEntities) {
//                        List<WorkEntity> entity = findCareGiverWorkingHoursIn4Weeks(mAppDatabase, cg.getId(), timeSlot);
//                        careGiverPriorities.put(cg.getId(), entity.size());
//                    }
//                    Map.Entry<Long, Integer> min = null;
//                    for (Map.Entry<Long, Integer> entry : careGiverPriorities.entrySet()) {
//                        if (min == null || entry.getValue() < min.getValue()) {
//                            min = entry;
//                        }
//                    }
//                    CareGiverEntity cgEnt = mAppDatabase.daoCareGiver().loadCareGiver(min.getKey());
//                    WorkEntity wEnt = createWork(room, timeSlot, cgEnt.getCareGiver());
//                    Calendar t = Calendar.getInstance();
//                    t.setTimeInMillis(timeSlot);
//                    String result = addWork(mAppDatabase, wEnt, t, cgEnt.getCareGiver());
////                    System.out.println("3 ======== result: " + result);
//                    if (result.equals("WorkAdded")) {
//                        continue timeSlot;
//                    }
//                }
//            }
        }
        List<WorkEntity> workEntities = mAppDatabase.daoWork().loadAll();
        System.out.println("================================= " + workEntities.size() + " Size ==================================");
    }

    public static void autoFill(AppDatabase mAppDatabase) {
        /**
         * The auto-fill feature should follow the criteria below:
         * 1. If multiple caregivers are eligible,follow the priority scale below:
         *      1. A caregiver with no overtime.
         *      2. A caregiver already working the same day,in the nearest room.
         *      3. A caregiver that has worked less hours in the past4 weeks.
         * 2. If no caregivers are available,the slot will stay vacant.
         **/

        Calendar cal = Calendar.getInstance();
        long[] times = getWeekStartAndEnd(cal);

        List<CareGiverEntity> careGiverEntities = mAppDatabase.daoCareGiver().loadAll();
        List<WorkEntity> workEntities = mAppDatabase.daoWork().loadAll();

        List<Long> timeSlots = new ArrayList<>();

        Calendar nextHour = Calendar.getInstance();
        nextHour.setTimeInMillis(times[0]);
        nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);
        long t = nextHour.getTimeInMillis();

        timeSlots.add(times[0]);
        while (times[0] <= t && t <= times[1]) {
            nextHour.setTimeInMillis(t);
            int getHour = nextHour.get(Calendar.HOUR_OF_DAY);
            if(getHour >= 9 && getHour <= 17) {
                timeSlots.add(t);
            }
            nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);
            t = nextHour.getTimeInMillis();
        }

        int i = 0;
        timeSlot: for(long timeSlot: timeSlots) {

//            for (CareGiverEntity cg: careGiverEntities) {
//                List<OvertimeEntity> overtime = mAppDatabase.daoOvertime()
//                        .loadOvertimesByCaregiverId(cg.getId(), times[0], times[1]);
//
//                // checking overtime within one week
//                if (overtime.size() == 0) {
//                    int p = 3;
//                    careGiverPriorities.put(cg.getId(), p);
//                }
//            }

//            Map<Long, Integer> careGiverPriorities = findCareGiversWithNoOvertime(mAppDatabase,
//                    careGiverEntities, times);

            room: for(int room = 1; room <= 10; room ++) {

                boolean isRoomFree = isRoomFree(mAppDatabase, room, timeSlot);
                if (!isRoomFree) {

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(timeSlot);

                    long[] dayStartEnd = getDayStartAndEndWorkingHours(c);

                    Map<Long, Integer> careGiverPriorities = new HashMap<>();
                    Map<Long, Integer> roomPref = new HashMap<>();

                    careGiver: for (CareGiverEntity cg: careGiverEntities) {

                        /**
                         * 1. A caregiver with no overtime.
                         **/
                        // checking overtime within one week
                        List<OvertimeEntity> overtime = mAppDatabase.daoOvertime()
                                .loadOvertimesByCaregiverId(cg.getId(), times[0], times[1]);

                        if (overtime.size() == 0) {
                            careGiverPriorities.put(cg.getId(), 1);

                            /**
                             * 2. A caregiver already working the same day,in the nearest room.
                             **/
                            // checking the work of caregiver in the same day
                            List<WorkEntity> works = mAppDatabase.daoWork()
                                    .loadWorkOfCareGiver(cg.getId(), dayStartEnd[0], dayStartEnd[1]);

                            // find the nearest room of caregiver
                            int distance = Math.abs(works.get(0).getRoom() - room);
                            int idx = 0;
                            for(int num = 1; num < works.size(); num++){
                                if (works.get(num).getStartTime() != timeSlot) {
                                    int val = careGiverPriorities.get(cg.getId()) == null ? 0 : careGiverPriorities.get(cg.getId()) + 1;
                                    careGiverPriorities.put(cg.getId(), val);

                                    int cDistance = Math.abs(works.get(num).getRoom() - room);
                                    if (cDistance < distance) {
                                        idx = num;
                                        distance = cDistance;
                                    }
                                }
                            }
                            int theNumber = works.get(idx).getRoom();
                            roomPref.put(cg.getId(), theNumber);

                            WorkEntity w = mAppDatabase.daoWork()
                                    .CareGiverWorkAtTime(cg.getEmail(), dayStartEnd[0]);
                            if (w != null) {
                                int val = careGiverPriorities.get(cg.getId()) == null ? 0 : careGiverPriorities.get(cg.getId()) + 1;
                                careGiverPriorities.put(cg.getId(), val);
                                continue room;
                            }

                            /**
                             * 3. A caregiver that has worked less hours in the past 4 weeks.
                             **/
                            List<WorkEntity> entity = findCareGiverWorkingHoursIn4Weeks(mAppDatabase, cg.getId(), timeSlot);
                            int val = careGiverPriorities.get(cg.getId()) == null ? 0 : careGiverPriorities.get(cg.getId()) + 1;
                            careGiverPriorities.put(cg.getId(), val);

                        }

                    }

                    /**
                     * The auto-fill feature should follow the criteria below:
                     * 1. If multiple caregivers are eligible,follow the priority scale below:
                     *      1. A caregiver with no overtime.
                     *      2. A caregiver already working the same day,in the nearest room.
                     *      3. A caregiver that has worked less hours in the past 4 weeks.
                     * 2. If no caregivers are available,the slot will stay vacant.
                     **/
                }
            }

        }
    }

    public static Map<Long, Integer> findCareGiversWithNoOvertime(AppDatabase mAppDatabase,
                                                                  List<CareGiverEntity> careGiverEntities, long[] times) {
        Map<Long, Integer> careGiverPriorities = new HashMap<>();

        for (CareGiverEntity cg: careGiverEntities) {
            List<OvertimeEntity> overtime = mAppDatabase.daoOvertime()
                    .loadOvertimesByCaregiverId(cg.getId(), times[0], times[1]);

            // checking overtime within one week
            if (overtime.size() == 0) {
                int p = 3;
                careGiverPriorities.put(cg.getId(), p);
            }
        }

        return careGiverPriorities;
    }

    public static List<WorkEntity> findCareGiverWorkingHoursIn4Weeks(AppDatabase mAppDatabase, long cgId, long time) {

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(time);
        start.set(Calendar.WEEK_OF_YEAR, start.get(Calendar.WEEK_OF_YEAR) - 4);
        start.set(Calendar.HOUR_OF_DAY, 9);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(time);
        end.set(Calendar.DAY_OF_YEAR, start.get(Calendar.DAY_OF_YEAR) - 1);
        end.set(Calendar.HOUR_OF_DAY, 16);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        List<WorkEntity> works = mAppDatabase.daoWork()
                .loadWorkOfCareGiver(cgId, start.getTimeInMillis(), end.getTimeInMillis());

        return works;
    }

    public static int findNearestRoom(List<WorkEntity> works, int selectedRoom, long time) {
        if (works.size() > 0) {
            int distance = Math.abs(works.get(0).getRoom() - selectedRoom);
            int idx = 0;
            for (int c = 1; c < works.size(); c++) {
                int cDistance = Math.abs(works.get(c).getRoom() - selectedRoom);
                if (cDistance < distance) {
                    idx = c;
                    distance = cDistance;
                }
            }
            int theNumber = works.get(idx).getRoom();
            return theNumber;
        }
        return 0;
    }

    public static long findClosest(Map<Long, Integer> mapCareGiverAndRoom, int selectedRoom) {
        if(mapCareGiverAndRoom.entrySet() != null && mapCareGiverAndRoom.entrySet().stream().findFirst() != null) {
            Optional<Map.Entry<Long, Integer>> entry = mapCareGiverAndRoom.entrySet().stream().findFirst();
            if(entry.isPresent()) {
                int distance = Math.abs(entry.get().getValue() - selectedRoom);
                long cgId = 0;
                for (Map.Entry<Long, Integer> c : mapCareGiverAndRoom.entrySet()) {
                    int cDistance = Math.abs(c.getValue() - selectedRoom);
                    if (cDistance < distance) {
                        cgId = c.getKey();
                        distance = cDistance;
                    }
                }
                return cgId;
            }
        }
        return 0;
    }

    public static WorkEntity createWork(int room, long time, CareGiver cg) {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setPatientName(getName());
        workEntity.setRoom(room);
        workEntity.setStartTime(time);
        Calendar endtime = Calendar.getInstance();
        endtime.setTimeInMillis(time);
        endtime.set(Calendar.HOUR_OF_DAY, endtime.get(Calendar.HOUR_OF_DAY) + 1);
        workEntity.setEndTime(endtime.getTimeInMillis());

        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time);

        return workEntity;
    }

    public static String getName() {
        Random rnd = new Random();
        String firstname = "James";
        String lastname = "Bond";

        String result;

        result = Character.toString(firstname.charAt(0)); // First char
        if (lastname.length() > 5)
            result += lastname.substring(0, 5);
        else
            result += lastname;
        result += Integer.toString(rnd.nextInt(99));
        System.out.println(result);
        return result;
    }
}