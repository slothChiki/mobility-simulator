-- 이미 데이터 있으면 스킵
INSERT INTO vehicles (vehicle_number, vehicle_grade, created_at)
SELECT * FROM (VALUES
                   ('서울01가1001', 'NORMAL', NOW()),
                   ('서울01나1002', 'NORMAL', NOW()),
                   ('서울01다1003', 'BLUE',   NOW()),
                   ('서울01라1004', 'NORMAL', NOW()),
                   ('서울01마1005', 'BLUE',   NOW()),
                   ('서울02가2001', 'NORMAL', NOW()),
                   ('서울02나2002', 'NORMAL', NOW()),
                   ('서울02다2003', 'BLACK',  NOW()),
                   ('서울02라2004', 'NORMAL', NOW()),
                   ('서울02마2005', 'BLUE',   NOW()),
                   ('서울03가3001', 'NORMAL', NOW()),
                   ('서울03나3002', 'NORMAL', NOW()),
                   ('서울03다3003', 'NORMAL', NOW()),
                   ('서울03라3004', 'BLUE',   NOW()),
                   ('서울03마3005', 'BLACK',  NOW()),
                   ('서울04가4001', 'NORMAL', NOW()),
                   ('서울04나4002', 'NORMAL', NOW()),
                   ('서울04다4003', 'NORMAL', NOW()),
                   ('서울04라4004', 'BLUE',   NOW()),
                   ('서울04마4005', 'NORMAL', NOW()),
                   ('서울05가5001', 'NORMAL', NOW()),
                   ('서울05나5002', 'BLACK',  NOW()),
                   ('서울05다5003', 'NORMAL', NOW()),
                   ('서울05라5004', 'NORMAL', NOW()),
                   ('서울05마5005', 'BLUE',   NOW()),
                   ('경기01가1001', 'NORMAL', NOW()),
                   ('경기01나1002', 'NORMAL', NOW()),
                   ('경기01다1003', 'BLUE',   NOW()),
                   ('경기01라1004', 'NORMAL', NOW()),
                   ('경기01마1005', 'NORMAL', NOW()),
                   ('경기02가2001', 'NORMAL', NOW()),
                   ('경기02나2002', 'BLACK',  NOW()),
                   ('경기02다2003', 'NORMAL', NOW()),
                   ('경기02라2004', 'NORMAL', NOW()),
                   ('경기02마2005', 'BLUE',   NOW()),
                   ('경기03가3001', 'NORMAL', NOW()),
                   ('경기03나3002', 'NORMAL', NOW()),
                   ('경기03다3003', 'NORMAL', NOW()),
                   ('경기03라3004', 'BLUE',   NOW()),
                   ('경기03마3005', 'NORMAL', NOW()),
                   ('경기04가4001', 'NORMAL', NOW()),
                   ('경기04나4002', 'NORMAL', NOW()),
                   ('경기04다4003', 'BLACK',  NOW()),
                   ('경기04라4004', 'NORMAL', NOW()),
                   ('경기04마4005', 'BLUE',   NOW()),
                   ('경기05가5001', 'NORMAL', NOW()),
                   ('경기05나5002', 'NORMAL', NOW()),
                   ('경기05다5003', 'NORMAL', NOW()),
                   ('경기05라5004', 'BLUE',   NOW()),
                   ('경기05마5005', 'NORMAL', NOW())
              ) AS tmp (vehicle_number, vehicle_grade, created_at)
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE vehicle_number = tmp.vehicle_number);

-- VehicleState 초기 데이터 (모든 차량 AVAILABLE)
INSERT INTO vehicle_states (vehicle_id, status, updated_at)
SELECT v.id, 'AVAILABLE', NOW()
FROM vehicles v
WHERE NOT EXISTS (SELECT 1 FROM vehicle_states vs WHERE vs.vehicle_id = v.id);