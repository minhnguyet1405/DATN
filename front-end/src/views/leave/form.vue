<template>
  <el-dialog
    class="dialog-leave"
    title="Thêm - Sửa lịch"
    :visible="dialogVisible"
    top="40px"
    width="574px"
    @close="handleCancel"
  >
    <div>
      <div>
        <el-form ref="form" :rules="rules" label-width="140px" size="mini" label-position="left" :model="form">
          <el-form-item prop="status" label="Trạng thái">
            <el-select v-model="form.status" placeholder="Chọn trạng thái">
              <el-option
                v-for="item in leaveStatus"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <div class="time-box">
            <el-form-item label="Từ" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="Bắt đầu"
                format="dd/MM/yyyy HH:mm"
              />
            </el-form-item>
            <span style="font-size:12px">(Giờ:phút)</span>
          </div>
          <div class="time-box">
            <el-form-item label="Đến" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="Kết thúc"
                format="dd/MM/yyyy HH:mm"
              />
            </el-form-item>
            <span style="font-size:12px">(Giờ:phút)</span>
          </div>

          <el-form-item label="Lý do">
            <el-input v-model="form.reason" />
          </el-form-item>
          <el-form-item label="Nguời duyệt đơn" prop="receive">
            <el-select v-model="form.receive" placeholder="Người duyệt đơn">
              <el-option
                v-for="item in userList"
                :key="item.uuid"
                :label="item.fullName"
                :value="item.uuid"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="d-flex">
        <div class="btn-box">
          <el-button
            size="small"
            type="default"
            class="btn-cancel pointer"
            @click="handleCancel"
          >Hủy bỏ</el-button>
          <el-button
            size="small"
            class="btn-add pointer"
            type="info"
            @click="handleSubmit"
          >Cập nhật</el-button>
        </div>

      </div>
    </div>
  </el-dialog>
</template>
<script>
import { inject } from 'vue'
import moment from 'moment'
import Cookies from 'js-cookie'
import axios from 'axios'
export default {
  name: 'Form',
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    },
    detailLeave: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      userList: [],
      loading: false,
      form: {
        id: null,
        status: null,
        startTime: moment(),
        endTime: moment(),
        reason: null,
        receive: null
      },
      leaveStatus: [
        { value: 'LEAVE', label: 'Nghỉ phép' },
        { value: 'SICK', label: 'Nghỉ ốm' },
        { value: 'ABSENT', label: 'Vắng mặt' }
      ],
      receiveList: [],
      rules: {
        status: [
          {
            required: true,
            message: 'Vui lòng chọn trạng thái',
            trigger: 'blur'
          }
        ],
        startTime: [
          {
            required: true,
            message: 'Vui lòng chọn thời gian bắt đầu',
            trigger: 'blur'
          }
        ],
        endTime: [
          {
            required: true,
            message: 'Vui lòng chọn thời gian kết thúc',
            trigger: 'blur'
          }
        ],
        receive: [
          {
            required: true,
            message: 'Vui lòng chọn người nhận mail',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    console.log('detailLeave', this.detailLeave)
    this.getUser()
  },
  methods: {
    handleCancel() {
      this.$emit('close')
    },
    resetDialog() {
      this.form = {
        id: null,
        status: null,
        startTime: moment(),
        endTime: moment(),
        reason: null,
        receive: null
      }
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
            page: 0,
            size: 100
          }
        })
        .then((res) => {
          if (res.data) {
            this.userList = res.data.data
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
    handleSubmit() {
      this.form = this.$root.trimData(this.form)
      this.$refs.form.validate((valid) => {
        if (valid) {
          const params = {
            status: this.form.status,
            startTime: moment(this.form.startTime).format('YYYY-MM-DD HH:mm:ss'),
            endTime: moment(this.form.endTime).format('YYYY-MM-DD HH:mm:ss'),
            reason: this.form.reason,
            receive: this.form.receive
          }
          const headers = {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + Cookies.get('access-token')
          }
          this.loading = true
          axios
            .post(
              process.env.VUE_APP_API + 'management/leave',
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
                this.$emit('success')
              } else {
                this.$message({
                  message: response.data.message,
                  type: 'error'
                })
                this.$emit('close')
              }
              this.loading = false
              this.resetDialog()
            })
            .catch((err) => {
              this.loading = false
              this.$message({
                message: err.response.data.message,
                type: 'error'
              })
              this.$emit('close')
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.btn-box{
  display: flex;
  justify-content: end;
  align-items: center;
  .el-button {
    height: 28px;
    padding: 5px 13px;
  }
}
.el-form-item {
  margin-bottom: 10;
}
::v-deep .el-dialog__body {
  padding: 10px 20px;
  background: #ebe7e7;
}
::v-deep .el-dialog__header {
  padding: 10px;
}
::v-deep .el-dialog__title {
  font-size: 14px;
  font-weight: 500;
}
::v-deep .el-dialog__headerbtn {
  top: 15px
}
.time-box {
  display: flex;
  gap: 20px;
}
</style>
