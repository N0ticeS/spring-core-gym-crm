INSERT INTO users (first_name, last_name, username, password, role, is_active, failed_login_attempts, locked_until)
VALUES ('John', 'Smith', 'John.Smith', '$2a$10$Bet9Dgr88vRp/xgkalTuxukIRBTto.DWQ/zuPPYh5HhhidZCGNGNm', 'TRAINER', true,
        0, null),
       ('Jane', 'Brown', 'Jane.Brown', '$2a$10$Bet9Dgr88vRp/xgkalTuxukIRBTto.DWQ/zuPPYh5HhhidZCGNGNm', 'TRAINER', true,
        0, null),
       ('Mike', 'Johnson', 'Mike.Johnson', '$2a$10$Bet9Dgr88vRp/xgkalTuxukIRBTto.DWQ/zuPPYh5HhhidZCGNGNm', 'TRAINEE',
        true, 0, null),
       ('Anna', 'Wilson', 'Anna.Wilson', '$2a$10$Bet9Dgr88vRp/xgkalTuxukIRBTto.DWQ/zuPPYh5HhhidZCGNGNm', 'TRAINEE',
        true, 0, null),
       ('Redis', 'Snow', 'Redis.Snow', '$2a$10$Bet9Dgr88vRp/xgkalTuxukIRBTto.DWQ/zuPPYh5HhhidZCGNGNm', 'ADMIN',
        true, 0, null);
