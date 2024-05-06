<!-- eslint-disable vue/valid-v-model -->
<template>
  <div class="body">
    <div class="mb-4">
      <div id="vehicle-table">
        <el-table
          class="table1"
          :data="topUserList"
          style="width: 100%"
        >
          <el-table-column label="" width="70px">
            <template slot-scope="{row}">
              <img :src="getUserById(row.receive).avatar">
            </template>
          </el-table-column>
          <el-table-column label="Họ và tên" width="220px">
            <template slot-scope="{row}">
              <div class="table-name">{{ getUserById(row.receive).fullName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="Phòng ban" width="400px">
            <template slot-scope="{row}">
              <div class="table-department">{{ getUserById(row.receive).department ? getUserById(row.receive).department.name : '' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="Trạng thái">
            <template slot-scope="{row}">
              <span class="table-number">{{ getStatusName(row.type) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Thời gian">
            <template slot-scope="{row}">
              <span class="table-number">{{ formatDate(row.startTime) }} - {{ formatDate(row.endTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Thời gian">
            <template slot-scope="{row}">
              <el-button v-if="row.isAccept === 0" type="danger" @click="handleApprove(row.id, getUserById(row.receive).fullName, 1)">Phê duyệt</el-button>
              <el-button v-if="row.isAccept === 1" type="success" @click="handleApprove(row.id, getUserById(row.receive).fullName, 0)">Đã phê duyệt</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import Cookies from 'js-cookie'
import user_default from '@/assets/images/user_default.png'
import { validEmail, validPhone } from '@/utils/validate'
import { inject } from 'vue'
import ImageCropper from '@/components/extra/ImageCropper'

export default {
  name: 'Accounts',
  components: {
  },
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  filters: {
    formatDatetime: function(value) {
      if (!value) return ''
      return moment(value, 'YYYY-MM-DD').format('DD/MM/YYYY')
    }
  },
  data() {
    return {
      loading: true,
      userList: [],
      topUserList: [],
      queryPage: {
        page: 0,
        size: 100,
        total: 0,
        search: ''
      },
      departmentList: [],
      leaveType: [
        { value: 'LEAVE', label: 'Nghỉ phép' },
        { value: 'SICK', label: 'Nghỉ ốm' },
        { value: 'ABSENT', label: 'Vắng mặt' }
      ]
    }
  },

  created() {
    this.getUser()
    this.getDepartment()
    this.getTopUser()
  },
  methods: {
    handleApprove(id, name, status) {
      this.$alert(name, 'Phê duyệt đăng ký' + ' ?', {
        confirmButtonText: 'Xác nhận',
        callback: action => {
          if (action === 'confirm') {
            this.approve(id, status)
          }
        }
      })
    },
    approve(id, status) {
      const params = {
        leaveId: id,
        status: status
      }
      const headers = {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .post(
          process.env.VUE_APP_API + 'management/leave-approve',
          params,
          { headers }
        )
        .then((response) => {
          if (
            response.data.status === 200 ||
                response.data.status === 201
          ) {
            this.$message({
              message: response.data.message,
              type: 'success'
            })
            this.getTopUser()
          } else {
            this.$message({
              message: response.data.message,
              type: 'error'
            })
          }
        })
        .catch((err) => {
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    getUser() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'user', {
          headers: headers,
          params: {
            page: this.queryPage.page > 0 ? this.queryPage.page - 1 : 0,
            size: this.queryPage.size,
            search: this.queryPage.search
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
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
    getUserById(id) {
      return this.userList.find(i => i.uuid === id)
    },
    getTopUser() {
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave-approve', {
          headers: headers
        })
        .then((res) => {
          if (res.data) {
            this.topUserList = res.data.data
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
    getStatusName(status) {
      const result = this.leaveType.find(i => {
        return i.value === status
      })
      return result?.label
    },
    formatDate(val) {
      return moment(val).format('HH:mm DD/MM/YYYY')
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
            page: 0,
            size: 100
          }
        })
        .then((res) => {
          if (res.data) {
            this.departmentList = res.data.data
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
    }
  }
}
</script>

<style lang="scss">
.el-dialog {
  margin-top: 30px !important;
}

.item-left {
  width: 48%;
  float: left;
  margin-right: 2%;
}

.item-right {
  width: 48%;
  float: left;
  margin-left: 2%;
}

.block-item {
  width: 100%;
  float: left;
}

#select-all-delete .el-icon-close {
  font-size: 14px;
  margin-left: 18px;
  margin-right: 29px;
  top: 4px;
  cursor: pointer;
}

.el-icon-camera-solid {
  font-size: 25px;
  color: #9a9a9a;
  position: relative;
  top: -50px;
  left: 54px;
  background: #dfdfdf;
  border-radius: 50%;
  padding: 8px;
  border: 1px solid white;
}
img{
  width: 50px;
  border-radius: 50%;
}
</style>
