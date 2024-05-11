<template>
  <div class="app-container">
    <div class="check-in-out-bock">
      <div class="filter">
        <el-date-picker
          v-model="filter.year"
          style="width: 100px"
          type="year"
          placeholder="Năm"
        />
        <el-date-picker
          v-model="filter.month"
          class="month"
          style="width: 120px; margin-left: 10px; height: 36px"
          format="MM"
          popper-class="custom-month"
          type="month"
          placeholder="Tháng"
        />
        <el-select
          v-model="filter.userId"
          style="width: 300px"
          filterable
          placeholder="Nhân viên"
          @change="handleSelectUser"
        >
          <el-option
            v-for="department in userList"
            :key="department.uuid"
            :label="department.fullName"
            :value="department.uuid"
          />
        </el-select>
        <div>
          <el-button
            class="btn-check"
            type="primary"
            :loading="loading"
            :disabled="disableBtn"
            @click="getList()"
          >
            Hiển thị dữ liệu
          </el-button>
        </div>
      </div>
    </div>
    <div class="table-bock">
      <el-table
        ref="multipleTable"
        v-loading="loading"
        :data="checkinList"
        style="width: 100%"
      >
        <el-table-column label="Ngày">
          <template slot-scope="scope"><span>{{ formatDate(scope.row.date) }}</span></template>
        </el-table-column>
        <el-table-column label="In">
          <template slot-scope="scope"><span>{{ formatTime(scope.row.timeIn) }}</span></template>
        </el-table-column>
        <el-table-column label="Out">
          <template slot-scope="scope"><span>{{ scope.row.timeOut ? formatTime(scope.row.timeOut) :'--' }}</span></template>
        </el-table-column>
        <el-table-column label="Chấm công">
          <template slot-scope="scope">
            <el-button type="danger" @click="openCheckin(scope.row)">Chấm công</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page.sync="queryPage.page"
        :page-sizes="[1, 5, 10, 15, 20]"
        :page-size="queryPage.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="queryPage.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      /></div>
    <CheckinHand v-if="dialogVisible" :dialog-visible="dialogVisible" :data-check-in="dataCheckIn" @close="handleClose" @success="handleSuccess" />
  </div>
</template>
<script>
import axios from 'axios'
import { inject } from 'vue'
import Cookies from 'js-cookie'
import moment from 'moment'
import CheckinHand from './components/formCheckIn.vue'
export default {
  name: 'Accounts',
  components: { CheckinHand },
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  data() {
    return {
      dataCheckIn: null,
      dialogVisible: false,
      disableBtn: true,
      loading: false,
      loading_checkin: false,
      filter: {
        department: null,
        userId: null,
        year: moment().year().toString(),
        month: (moment().month() + 1).toString()
      },
      checkinList: [],
      departmentList: [],
      userList: [],
      queryPage: {
        page: 0,
        size: 10,
        total: 0
      }
    }
  },
  created() {
    this.getDepartment()
    this.getUserByDepartment()
  },
  methods: {
    handleClose() {
      this.dataCheckIn = null
      this.dialogVisible = false
    },
    handleSuccess() {
      this.dataCheckIn = null
      this.dialogVisible = false
      this.getList()
    },
    getList() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/check-in-out', {
          headers: headers,
          params: {
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            userId: this.filter.userId,
            month: (moment(this.filter.month).month() + 1).toString(),
            year: (moment(this.filter.year).year()).toString()
          }
        })
        .then((res) => {
          if (res.data) {
            this.checkinList = res.data.data
            this.queryPage.total = res.data.total
            this.loading = false
          }
        })
        .catch((err) => {
          console.log(err)
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getDepartment() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/department', {
          headers: headers,
          params: {
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            search: this.queryPage.search
          }
        })
        .then((res) => {
          if (res.data) {
            this.departmentList = res.data.data
            this.loading = false
          }
        })
        .catch((err) => {
          console.log(err)
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getUserByDepartment() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user/by-manager', {
          headers: headers
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
            console.log('this.userList', this.userList)
          }
        })
        .catch((err) => {
          console.log(err)
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    openCheckin(row) {
      this.dataCheckIn = row
      this.dialogVisible = true
    },
    handleSizeChange(size) {
      this.queryPage.size = size
      this.getList()
    },
    handleCurrentChange(page) {
      this.queryPage.page = page
      this.getList()
    },
    formatDate(val) {
      return moment(val).format('DD/MM/YYYY')
    },
    formatTime(val) {
      return val ? moment(val).format('HH:mm') : ''
    },
    handleSelectUser() {
      if (this.filter.userId) {
        this.disableBtn = false
      } else {
        this.disableBtn = true
      }
    }
  }
}
</script>
<style lang="scss">
.check-in-out-bock {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.btn-check {
  background-color: rgb(67, 35, 126);
  border-color: rgb(67, 35, 126);
}
.filter {
  display: flex;
  gap: 15px;
}
</style>
