package com.sorted.commons.manage.otp;

import com.sorted.commons.constants.Defaults;
import com.sorted.commons.entity.mongo.BaseMongoEntity;
import com.sorted.commons.entity.mongo.Otp;
import com.sorted.commons.entity.mongo.Role;
import com.sorted.commons.entity.mongo.Users;
import com.sorted.commons.entity.service.Otp_Service;
import com.sorted.commons.entity.service.RoleService;
import com.sorted.commons.entity.service.Users_Service;
import com.sorted.commons.enums.ProcessType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.enums.UserType;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;
import com.sorted.commons.helper.AggregationFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilter;
import com.sorted.commons.helper.AggregationFilter.SEFilterType;
import com.sorted.commons.helper.AggregationFilter.WhereClause;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ManageOTPManagerService {

    private final Users_Service users_Service;

    private final RoleService roleService;

    private final ManageOtp manageOtp;

    private final Otp_Service otp_Service;

    @Value("${se.portal.otp_length}")
    private int otp_length;

    public String send(@NonNull String mobile_number, @NonNull ProcessType process_type, String cud_by) {
        boolean isSeller = isSeller(mobile_number);
        return manageOtp.generateAndSaveOtp(mobile_number, process_type, cud_by, isSeller ? 6 : otp_length);
    }

    public String resendOtp(@NonNull ProcessType process, @NonNull String uuid) {
        SEFilter filterO = new SEFilter(SEFilterType.AND);
        filterO.addClause(WhereClause.eq(Otp.Fields.process_type, process.name()));
        filterO.addClause(WhereClause.eq(Otp.Fields.status, true));
        filterO.addClause(WhereClause.eq(Otp.Fields.is_verified, false));
        filterO.addClause(WhereClause.eq(Otp.Fields.uuid, uuid));
        filterO.addClause(WhereClause.eq(BaseMongoEntity.Fields.deleted, false));

        Otp oldOtp = otp_Service.repoFindOne(filterO);
        if (oldOtp == null) {
            throw new CustomIllegalArgumentsException(ResponseCode.INVALID_RESEND_REQUEST);
        }
        oldOtp.setStatus(false);
        otp_Service.update(oldOtp.getId(), oldOtp, Defaults.RESEND);
        return this.send(oldOtp.getMobile_no(), process, Defaults.RESEND);
    }

    public boolean isSeller(String mobileNo) {
        AggregationFilter.SEFilter filter = new AggregationFilter.SEFilter(AggregationFilter.SEFilterType.AND);
        filter.addClause(AggregationFilter.WhereClause.eq(BaseMongoEntity.Fields.deleted, false));
        filter.addClause(AggregationFilter.WhereClause.eq(Users.Fields.mobile_no, mobileNo));

        Users users = users_Service.repoFindOne(filter);
        Optional<Role> optional = roleService.findById(users.getRole_id());
        if (optional.isPresent()) {
            Role role = optional.get();
            return role.getUser_type().equals(UserType.SELLER);
        }
        return false;
    }
}
