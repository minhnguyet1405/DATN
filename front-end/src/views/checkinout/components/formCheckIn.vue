<template>
  <el-dialog
    class="dialog-checkin"
    title="Chọn thời gian"
    :visible="dialogVisible"
    top="40px"
    width="574px"
    @close="handleCancel"
  >
    <div>
      <div class="time-box">
        <el-time-picker
          v-model="checkin.startTime"
          format="HH:mm"
          placeholder="Bắt đầu"
        />
        <div>Đến</div>
        <el-time-picker
          v-model="checkin.endTime"
          format="HH:mm"
          placeholder="Bắt đầu"
        />
      </div>
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
    dataCheckIn: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      checkin: {
        startTime: moment().format('08:00'),
        endTime: moment().format('21:00')
      }
    }
  },
  created() {
    console.log(this.dataCheckIn)
    if (this.dataCheckIn.timeIn) {
      this.checkin.startTime = this.dataCheckIn.timeIn
    }
    if (this.dataCheckIn.timeOut) {
      this.checkin.endTime = this.dataCheckIn.timeOut
    }
  },
  methods: {
    handleCancel() {
      this.$emit('close')
      this.resetDialog()
    },
    resetDialog() {
    },

    handleSubmit() {
      const params = {
        date: this.dataCheckIn.date,
        userId: this.dataCheckIn.userId,
        startTime: moment(this.checkin.startTime).format('HH:mm'),
        endTime: moment(this.checkin.endTime).format('HH:mm')
      }
      const headers = {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .post(
          process.env.VUE_APP_API + 'management/check-in-hand',
          params,
          { headers }
        )
        .then((response) => {
          this.$message.success('Chấm công thành công')
          this.$emit('success')
        })
        .catch((err) => {
          this.$message({
            message: err.response.message,
            type: 'error'
          })
          this.$emit('close')
        })
    }
  }
}
</script>
<style lang="scss" scoped>
.time-box{
  display: flex;
  align-items: center;
}
.btn-box{
  display: flex;
  margin-top: 20px;
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
